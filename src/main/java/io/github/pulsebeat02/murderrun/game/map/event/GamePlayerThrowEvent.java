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
package io.github.pulsebeat02.murderrun.game.map.event;

import static java.util.Objects.requireNonNull;

import io.github.pulsebeat02.murderrun.game.Game;
import io.github.pulsebeat02.murderrun.game.GameProperties;
import io.github.pulsebeat02.murderrun.game.GameSettings;
import io.github.pulsebeat02.murderrun.game.arena.Arena;
import io.github.pulsebeat02.murderrun.game.map.GameMap;
import io.github.pulsebeat02.murderrun.game.map.TruckManager;
import io.github.pulsebeat02.murderrun.game.map.part.CarPart;
import io.github.pulsebeat02.murderrun.game.map.part.PartsManager;
import io.github.pulsebeat02.murderrun.game.player.*;
import io.github.pulsebeat02.murderrun.locale.Message;
import io.github.pulsebeat02.murderrun.utils.PDCUtils;
import io.github.pulsebeat02.murderrun.utils.item.ItemFactory;
import net.kyori.adventure.text.Component;
import org.bukkit.Location;
import org.bukkit.entity.Item;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.player.PlayerDropItemEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.PlayerInventory;

public final class GamePlayerThrowEvent extends GameEvent {

  public GamePlayerThrowEvent(final Game game) {
    super(game);
  }

  @EventHandler(priority = EventPriority.LOWEST)
  public void onKillerThrowItem(final PlayerDropItemEvent event) {
    final Game game = this.getGame();
    final GamePlayerManager playerManager = game.getPlayerManager();
    final Player player = event.getPlayer();
    if (!playerManager.checkPlayerExists(player)) {
      return;
    }

    final Item item = event.getItemDrop();
    final ItemStack stack = item.getItemStack();
    final ItemStack sword = ItemFactory.createKillerSword();
    if (stack.isSimilar(sword)) {
      event.setCancelled(true);
      return;
    }

    final ItemStack arrow = ItemFactory.createKillerArrow();
    if (stack.isSimilar(arrow)) {
      event.setCancelled(true);
      return;
    }
  }

  @EventHandler(priority = EventPriority.LOWEST)
  public void onPlayerThrowItem(final PlayerDropItemEvent event) {
    final Item item = event.getItemDrop();
    final ItemStack stack = item.getItemStack();
    if (!PDCUtils.isCarPart(stack)) {
      return;
    }

    final Game game = this.getGame();
    final GameSettings configuration = game.getSettings();
    final Arena arena = requireNonNull(configuration.getArena());
    final Location truckLocation = arena.getTruck();
    final Location itemLocation = item.getLocation();
    final double distSquared = itemLocation.distanceSquared(truckLocation);
    final double radius = GameProperties.CAR_PART_TRUCK_RADIUS;
    final double squared = radius * radius;
    if (distSquared > squared) {
      event.setCancelled(true);
      return;
    }

    item.remove();

    final GameMap map = game.getMap();
    final PartsManager manager = map.getCarPartManager();
    final CarPart carPart = manager.getCarPartItemStack(stack);
    if (carPart == null) {
      return;
    }

    manager.removeCarPart(carPart);

    final GamePlayerManager playerManager = game.getPlayerManager();
    final Player player = event.getPlayer();

    if (playerManager.checkPlayerExists(player)) {
      final GamePlayer gamePlayer = playerManager.getGamePlayer(player);
      if (gamePlayer instanceof final Survivor survivor) {
        final int retrieved = survivor.getCarPartsRetrieved();
        survivor.setCarPartsRetrieved(retrieved + 1);
      }
    }

    final int leftOver = manager.getRemainingParts();
    if (leftOver == 0) {
      final TruckManager truck = map.getTruckManager();
      truck.startTruckFixTimer();
      return;
    }

    this.announceCarPartRetrieval(leftOver);
    this.setScoreboard();
    if (!this.checkIfPlayerStillHasCarPart(player)) {
      this.setPlayerCarPartStatus(player);
    }
  }

  private void setScoreboard() {
    final Game game = this.getGame();
    final GamePlayerManager manager = game.getPlayerManager();
    manager.applyToAllParticipants(player -> {
      final MetadataManager metadata = player.getMetadataManager();
      final PlayerScoreboard scoreboard = metadata.getSidebar();
      scoreboard.updateSidebar();
    });
  }

  private void announceCarPartRetrieval(final int leftOver) {
    final Game game = this.getGame();
    final Component title = Message.CAR_PART_ITEM_RETRIEVAL.build(leftOver);
    final GamePlayerManager manager = game.getPlayerManager();
    manager.sendMessageToAllParticipants(title);
    manager.playSoundForAllParticipants("block.anvil.use");
  }

  private boolean checkIfPlayerStillHasCarPart(final Player thrower) {
    final PlayerInventory inventory = thrower.getInventory();
    final ItemStack[] contents = inventory.getContents();
    for (final ItemStack slot : contents) {
      if (PDCUtils.isCarPart(slot)) {
        return true;
      }
    }
    return false;
  }

  private void setPlayerCarPartStatus(final Player thrower) {
    if (!this.isGamePlayer(thrower)) {
      return;
    }

    final Game game = this.getGame();
    final GamePlayerManager manager = game.getPlayerManager();
    final GamePlayer player = manager.getGamePlayer(thrower);
    if (player instanceof final Survivor survivor) {
      survivor.setHasCarPart(false);
    }
  }
}
