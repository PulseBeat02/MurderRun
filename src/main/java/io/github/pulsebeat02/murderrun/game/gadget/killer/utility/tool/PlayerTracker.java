/*

MIT License

Copyright (c) 2024 Brandon Li

Permission is hereby granted, free of charge, to any person obtaining a copy
of this software and associated documentation files (the "Software"), to deal
in the Software without restriction, including without limitation the rights
to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
copies of the Software, and to permit persons to whom the Software is
furnished to do so, subject to the following conditions:

The above copyright notice and this permission notice shall be included in all
copies or substantial portions of the Software.

THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE
SOFTWARE.

*/
package io.github.pulsebeat02.murderrun.game.gadget.killer.utility.tool;

import static java.util.Objects.requireNonNull;

import io.github.pulsebeat02.murderrun.game.Game;
import io.github.pulsebeat02.murderrun.game.GameProperties;
import io.github.pulsebeat02.murderrun.game.gadget.killer.KillerGadget;
import io.github.pulsebeat02.murderrun.game.gadget.packet.GadgetDropPacket;
import io.github.pulsebeat02.murderrun.game.gadget.packet.GadgetRightClickPacket;
import io.github.pulsebeat02.murderrun.game.player.GamePlayer;
import io.github.pulsebeat02.murderrun.game.player.PlayerAudience;
import io.github.pulsebeat02.murderrun.game.player.PlayerManager;
import io.github.pulsebeat02.murderrun.immutable.Keys;
import io.github.pulsebeat02.murderrun.locale.Message;
import io.github.pulsebeat02.murderrun.utils.InventoryUtils;
import io.github.pulsebeat02.murderrun.utils.PDCUtils;
import io.github.pulsebeat02.murderrun.utils.item.ItemFactory;
import java.util.Collection;
import java.util.stream.Stream;
import net.kyori.adventure.text.Component;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.NamespacedKey;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.PlayerInventory;
import org.bukkit.persistence.PersistentDataType;

public final class PlayerTracker extends KillerGadget {

  public PlayerTracker() {
    super(
      "player_tracker",
      Material.COMPASS,
      Message.PLAYER_TRACKER_NAME.build(),
      Message.PLAYER_TRACKER_LORE.build(),
      GameProperties.PLAYER_TRACKER_COST,
      ItemFactory::createPlayerTracker
    );
  }

  @Override
  public boolean onGadgetDrop(final GadgetDropPacket packet) {
    return true;
  }

  @Override
  public boolean onGadgetRightClick(final GadgetRightClickPacket packet) {
    final Game game = packet.getGame();
    final PlayerManager manager = game.getPlayerManager();
    final GamePlayer player = packet.getPlayer();
    final Location location = player.getLocation();
    final int distance = (int) Math.round(this.getNearestSurvivorDistance(manager, location));
    final ItemStack stack = packet.getItemStack();
    final int count = this.increaseAndGetSurvivorCount(stack);
    final boolean destroy = count >= GameProperties.PLAYER_TRACKER_USES;
    if (destroy) {
      this.resetTrackerCount(stack);
      final PlayerInventory inventory = player.getInventory();
      InventoryUtils.consumeStack(inventory, stack);
    }

    final PlayerAudience audience = player.getAudience();
    final Component message = Message.PLAYER_TRACKER_ACTIVATE.build(distance);
    audience.sendMessage(message);
    audience.playSound(GameProperties.PLAYER_TRACKER_SOUND);

    return false;
  }

  private void resetTrackerCount(final ItemStack stack) {
    final NamespacedKey key = Keys.PLAYER_TRACKER;
    final PersistentDataType<Integer, Integer> type = PersistentDataType.INTEGER;
    PDCUtils.setPersistentDataAttribute(stack, key, type, 0);
  }

  private int increaseAndGetSurvivorCount(final ItemStack stack) {
    final NamespacedKey key = Keys.PLAYER_TRACKER;
    final PersistentDataType<Integer, Integer> type = PersistentDataType.INTEGER;
    final Integer val = requireNonNull(PDCUtils.getPersistentDataAttribute(stack, key, type));
    final int count = val + 1;
    PDCUtils.setPersistentDataAttribute(stack, key, type, count);
    return count;
  }

  private double getNearestSurvivorDistance(final PlayerManager manager, final Location origin) {
    double min = Double.MAX_VALUE;
    final Stream<GamePlayer> survivors = manager.getLivingInnocentPlayers();
    final Collection<GamePlayer> collection = survivors.toList();
    for (final GamePlayer survivor : collection) {
      final Location location = survivor.getLocation();
      final double distance = location.distance(origin);
      if (distance < min) {
        min = distance;
      }
    }
    return min;
  }
}
