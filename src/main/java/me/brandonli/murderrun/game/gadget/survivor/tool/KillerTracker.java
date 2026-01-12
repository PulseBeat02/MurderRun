/*
 * This file is part of Murder Run, a spin-off game-mode of Dead by Daylight
 * Copyright (C) Brandon Li <https://brandonli.me/>
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program.  If not, see <https://www.gnu.org/licenses/>.
 */
package me.brandonli.murderrun.game.gadget.survivor.tool;

import static java.util.Objects.requireNonNull;

import java.util.Collection;
import java.util.stream.Stream;
import me.brandonli.murderrun.game.Game;
import me.brandonli.murderrun.game.GameProperties;
import me.brandonli.murderrun.game.gadget.packet.GadgetDropPacket;
import me.brandonli.murderrun.game.gadget.packet.GadgetRightClickPacket;
import me.brandonli.murderrun.game.gadget.survivor.SurvivorGadget;
import me.brandonli.murderrun.game.player.GamePlayer;
import me.brandonli.murderrun.game.player.GamePlayerManager;
import me.brandonli.murderrun.game.player.PlayerAudience;
import me.brandonli.murderrun.locale.Message;
import me.brandonli.murderrun.utils.InventoryUtils;
import me.brandonli.murderrun.utils.PDCUtils;
import me.brandonli.murderrun.utils.immutable.Keys;
import me.brandonli.murderrun.utils.item.ItemFactory;
import net.kyori.adventure.text.Component;
import org.bukkit.Location;
import org.bukkit.NamespacedKey;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.PlayerInventory;
import org.bukkit.persistence.PersistentDataType;

public final class KillerTracker extends SurvivorGadget {

  public KillerTracker(final Game game) {
    final GameProperties properties = game.getProperties();
    super(
      "killer_tracker",
      properties.getKillerTrackerCost(),
      ItemFactory.createKillerTracker(
        ItemFactory.createGadget(
          "killer_tracker",
          properties.getKillerTrackerMaterial(),
          Message.KILLER_TRACKER_NAME.build(),
          Message.KILLER_TRACKER_LORE.build()
        )
      )
    );
  }

  @Override
  public boolean onGadgetDrop(final GadgetDropPacket packet) {
    return true;
  }

  @Override
  public boolean onGadgetRightClick(final GadgetRightClickPacket packet) {
    final Game game = packet.getGame();
    final GamePlayerManager manager = game.getPlayerManager();
    final GamePlayer player = packet.getPlayer();
    final Location location = player.getLocation();
    final int distance = (int) Math.round(this.getNearestKillerDistance(manager, location));
    final ItemStack stack = packet.getItemStack();
    final int count = this.increaseAndGetKillerCount(stack);
    final GameProperties properties = game.getProperties();
    final boolean destroy = count >= properties.getKillerTrackerUses();
    if (destroy) {
      this.resetTrackerCount(stack);
      final PlayerInventory inventory = player.getInventory();
      InventoryUtils.consumeStack(inventory, stack);
    }

    final PlayerAudience audience = player.getAudience();
    final Component message = Message.KILLER_TRACKER_ACTIVATE.build(distance);
    audience.sendMessage(message);
    audience.playSound(properties.getKillerTrackerSound());

    return false;
  }

  private void resetTrackerCount(final ItemStack stack) {
    final NamespacedKey key = Keys.PLAYER_TRACKER;
    final PersistentDataType<Integer, Integer> type = PersistentDataType.INTEGER;
    PDCUtils.setPersistentDataAttribute(stack, key, type, 0);
  }

  private int increaseAndGetKillerCount(final ItemStack stack) {
    final NamespacedKey key = Keys.KILLER_TRACKER;
    final PersistentDataType<Integer, Integer> type = PersistentDataType.INTEGER;
    final Integer val = requireNonNull(PDCUtils.getPersistentDataAttribute(stack, key, type));
    final int count = val + 1;
    PDCUtils.setPersistentDataAttribute(stack, key, type, count);
    return count;
  }

  private double getNearestKillerDistance(final GamePlayerManager manager, final Location origin) {
    double min = Double.MAX_VALUE;
    final Stream<GamePlayer> killers = manager.getKillers();
    final Collection<GamePlayer> collection = killers.toList();
    for (final GamePlayer killer : collection) {
      final Location location = killer.getLocation();
      final double distance = location.distance(origin);
      if (distance < min) {
        min = distance;
      }
    }
    return min;
  }
}
