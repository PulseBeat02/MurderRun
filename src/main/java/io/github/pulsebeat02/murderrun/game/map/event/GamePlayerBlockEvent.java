package io.github.pulsebeat02.murderrun.game.map.event;

import io.github.pulsebeat02.murderrun.game.Game;
import io.github.pulsebeat02.murderrun.game.map.BlockWhitelistManager;
import io.github.pulsebeat02.murderrun.game.map.Map;
import io.github.pulsebeat02.murderrun.game.player.GamePlayer;
import io.github.pulsebeat02.murderrun.game.player.Killer;
import io.github.pulsebeat02.murderrun.game.player.PlayerManager;
import io.github.pulsebeat02.murderrun.game.player.Survivor;
import io.github.pulsebeat02.murderrun.resourcepack.sound.SoundResource;
import io.github.pulsebeat02.murderrun.resourcepack.sound.Sounds;
import io.github.pulsebeat02.murderrun.utils.PDCUtils;
import io.github.pulsebeat02.murderrun.utils.item.Item;
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
    final PlayerManager manager = game.getPlayerManager();
    final GamePlayer gamePlayer = manager.getGamePlayer(player);
    if (gamePlayer instanceof final Killer killer) {
      if (!killer.canForceMineBlocks()) {
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
      final Map map = game.getMap();
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
    final PlayerManager manager = game.getPlayerManager();
    final GamePlayer murderer = manager.getGamePlayer(player);
    if (murderer instanceof Survivor) {
      return;
    }

    final Block block = event.getBlock();
    final Material material = block.getType();
    if (material == Material.BEDROCK) {
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
    final PlayerManager manager = game.getPlayerManager();
    final GamePlayer gamePlayer = manager.getGamePlayer(player);
    if (gamePlayer instanceof final Survivor survivor) {
      if (survivor.canPlaceBlocks()) {
        final Map map = game.getMap();
        final BlockWhitelistManager whitelist = map.getBlockWhitelistManager();
        final Block block = event.getBlock();
        whitelist.addWhitelistedBlock(block);
        return;
      }
    }

    event.setCancelled(true);
  }
}
