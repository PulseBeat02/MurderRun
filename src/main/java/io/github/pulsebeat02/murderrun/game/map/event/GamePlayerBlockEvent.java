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

import io.github.pulsebeat02.murderrun.game.Game;
import io.github.pulsebeat02.murderrun.game.GameProperties;
import io.github.pulsebeat02.murderrun.game.GameStatus;
import io.github.pulsebeat02.murderrun.game.map.BlockWhitelistManager;
import io.github.pulsebeat02.murderrun.game.map.GameMap;
import io.github.pulsebeat02.murderrun.game.player.GamePlayer;
import io.github.pulsebeat02.murderrun.game.player.GamePlayerManager;
import io.github.pulsebeat02.murderrun.game.player.Killer;
import io.github.pulsebeat02.murderrun.game.player.Survivor;
import io.github.pulsebeat02.murderrun.resourcepack.sound.SoundResource;
import io.github.pulsebeat02.murderrun.resourcepack.sound.Sounds;
import io.github.pulsebeat02.murderrun.utils.PDCUtils;
import io.github.pulsebeat02.murderrun.utils.item.Item;
import java.util.Collection;
import java.util.HashSet;
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

  private static final Collection<Material> BLACKLISTED_BLOCKS;

  static {
    BLACKLISTED_BLOCKS = new HashSet<>();
    final String raw = GameProperties.KILLER_CANNOT_BREAK;
    final String[] individual = raw.split(",");
    for (final String material : individual) {
      final String upper = material.toUpperCase();
      final Material target = Material.getMaterial(upper);
      if (target != null) {
        BLACKLISTED_BLOCKS.add(Material.valueOf(material));
      }
    }
  }

  public GamePlayerBlockEvent(final Game game) {
    super(game);
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
    if (BLACKLISTED_BLOCKS.contains(material)) {
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
