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
package me.brandonli.murderrun.game.map.event;

import static java.util.Objects.requireNonNull;

import me.brandonli.murderrun.game.Game;
import me.brandonli.murderrun.game.GameProperties;
import me.brandonli.murderrun.game.GameSettings;
import me.brandonli.murderrun.game.arena.Arena;
import me.brandonli.murderrun.game.map.GameMap;
import me.brandonli.murderrun.game.map.TruckManager;
import me.brandonli.murderrun.game.map.part.CarPart;
import me.brandonli.murderrun.game.map.part.PartsManager;
import me.brandonli.murderrun.game.player.*;
import me.brandonli.murderrun.game.player.metadata.MetadataManager;
import me.brandonli.murderrun.locale.Message;
import me.brandonli.murderrun.utils.PDCUtils;
import me.brandonli.murderrun.utils.item.ItemFactory;
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
    final GameProperties properties = game.getProperties();
    final ItemStack sword = ItemFactory.createKillerSword(properties);
    if (stack.isSimilar(sword)) {
      event.setCancelled(true);
      return;
    }

    final ItemStack arrow = ItemFactory.createKillerArrow(properties);
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
    final GameProperties properties = game.getProperties();
    final double radius = properties.getCarPartTruckRadius();
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
