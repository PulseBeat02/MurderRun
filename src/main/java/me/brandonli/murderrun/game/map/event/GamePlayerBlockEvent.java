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

import java.util.Collection;
import java.util.HashSet;
import me.brandonli.murderrun.game.Game;
import me.brandonli.murderrun.game.GameProperties;
import me.brandonli.murderrun.game.GameStatus;
import me.brandonli.murderrun.game.map.BlockWhitelistManager;
import me.brandonli.murderrun.game.map.GameMap;
import me.brandonli.murderrun.game.player.GamePlayer;
import me.brandonli.murderrun.game.player.GamePlayerManager;
import me.brandonli.murderrun.game.player.Killer;
import me.brandonli.murderrun.game.player.Survivor;
import me.brandonli.murderrun.resourcepack.sound.SoundResource;
import me.brandonli.murderrun.resourcepack.sound.Sounds;
import me.brandonli.murderrun.utils.PDCUtils;
import me.brandonli.murderrun.utils.item.Item;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.event.block.BlockDamageEvent;
import org.bukkit.event.block.BlockDropItemEvent;
import org.bukkit.event.block.BlockPlaceEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.PlayerInventory;

public final class GamePlayerBlockEvent extends GameEvent {

  private final Collection<Material> blacklisted;

  public GamePlayerBlockEvent(final Game game) {
    super(game);
    final GameProperties properties = game.getProperties();
    final String raw = properties.getKillerCannotBreak();
    this.blacklisted = new HashSet<>();
    final String[] individual = raw.split(",");
    for (final String material : individual) {
      final String upper = material.toUpperCase();
      final Material target = Material.getMaterial(upper);
      if (target != null) {
        this.blacklisted.add(Material.valueOf(material));
      }
    }
  }

  @EventHandler(priority = EventPriority.LOWEST)
  private void onBlockDropEvent(final BlockDropItemEvent event) {
    final Player player = event.getPlayer();
    if (this.isGamePlayer(player)) {
      return;
    }

    event.setCancelled(true);
  }

  @EventHandler(priority = EventPriority.LOWEST)
  private void onBlockDestroy(final BlockBreakEvent event) {
    final Player player = event.getPlayer();
    if (!this.isGamePlayer(player)) {
      return;
    }

    final Game game = this.getGame();
    final GamePlayerManager manager = game.getPlayerManager();
    final GamePlayer gamePlayer = manager.getGamePlayer(player);
    if (gamePlayer instanceof final Killer killer) {
      if (!killer.canForceMineBlocks()) {
        event.setCancelled(true);
        return;
      }

      final GameStatus status = game.getStatus();
      final GameStatus.Status gameStatus = status.getStatus();
      if (gameStatus == GameStatus.Status.SURVIVORS_RELEASED) {
        event.setCancelled(true);
        return;
      }

      event.setDropItems(false);
      event.setExpToDrop(0);

      final Location murdererLocation = gamePlayer.getLocation();
      final SoundResource sound = Sounds.CHAINSAW;
      manager.stopSoundsForAllParticipants(sound);
      manager.playSoundForAllParticipantsAtLocation(murdererLocation, sound);
    } else {
      final PlayerInventory inventory = player.getInventory();
      final ItemStack hand = inventory.getItemInMainHand();
      if (PDCUtils.canBreakMapBlocks(hand)) {
        event.setDropItems(false);
        event.setExpToDrop(0);
        return;
      }

      final Block block = event.getBlock();
      final GameMap map = game.getMap();
      final BlockWhitelistManager whitelistManager = map.getBlockWhitelistManager();
      final boolean canBreak = whitelistManager.checkAndRemoveBlock(block);
      if (!canBreak) {
        event.setCancelled(true);
      }

      event.setDropItems(false);
      event.setExpToDrop(0);
    }
  }

  @EventHandler(priority = EventPriority.LOWEST)
  private void onBlockDamage(final BlockDamageEvent event) {
    final Player player = event.getPlayer();
    if (!this.isGamePlayer(player)) {
      return;
    }

    final PlayerInventory inventory = player.getInventory();
    final ItemStack hand = inventory.getItemInMainHand();
    final Game game = this.getGame();
    final GamePlayerManager manager = game.getPlayerManager();
    final GamePlayer murderer = manager.getGamePlayer(player);
    if (murderer instanceof Survivor) {
      return;
    }

    final Block block = event.getBlock();
    final Material material = block.getType();
    if (this.blacklisted.contains(material)) {
      event.setCancelled(true);
      return;
    }

    if (!PDCUtils.canBreakMapBlocks(hand)) {
      event.setCancelled(true);
      return;
    }

    Item.builder(hand).useOneDurability();
    event.setInstaBreak(true);
  }

  @EventHandler(priority = EventPriority.LOWEST)
  private void onBlackPlace(final BlockPlaceEvent event) {
    final Player player = event.getPlayer();
    if (!this.isGamePlayer(player)) {
      return;
    }

    final Game game = this.getGame();
    final GamePlayerManager manager = game.getPlayerManager();
    final GamePlayer gamePlayer = manager.getGamePlayer(player);
    if (gamePlayer instanceof final Survivor survivor && survivor.canPlaceBlocks()) {
      final GameMap map = game.getMap();
      final BlockWhitelistManager whitelist = map.getBlockWhitelistManager();
      final Block block = event.getBlock();
      whitelist.addWhitelistedBlock(block);
      return;
    }

    event.setCancelled(true);
  }
}
