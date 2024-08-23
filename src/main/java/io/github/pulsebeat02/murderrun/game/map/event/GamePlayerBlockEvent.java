package io.github.pulsebeat02.murderrun.game.map.event;

import io.github.pulsebeat02.murderrun.game.Game;
import io.github.pulsebeat02.murderrun.game.player.GamePlayer;
import io.github.pulsebeat02.murderrun.game.player.Killer;
import io.github.pulsebeat02.murderrun.game.player.PlayerManager;
import io.github.pulsebeat02.murderrun.game.player.Survivor;
import io.github.pulsebeat02.murderrun.resourcepack.sound.SoundResource;
import io.github.pulsebeat02.murderrun.resourcepack.sound.Sounds;
import io.github.pulsebeat02.murderrun.utils.PDCUtils;
import io.github.pulsebeat02.murderrun.utils.item.Item;
import java.util.Set;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.event.block.BlockDamageEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.PlayerInventory;

public final class GamePlayerBlockEvent implements Listener {

  private final Game game;
  private final Set<Material> whitelisted;

  public GamePlayerBlockEvent(final Game game) {
    this.game = game;
    this.whitelisted = Set.of(Material.PACKED_ICE);
  }

  public Game getGame() {
    return this.game;
  }

  @EventHandler(priority = EventPriority.LOWEST)
  private void onBlockDestroy(final BlockBreakEvent event) {

    final Player player = event.getPlayer();
    final PlayerManager manager = this.game.getPlayerManager();
    final boolean valid = manager.checkPlayerExists(player);
    if (!valid) {
      return;
    }

    final GamePlayer gamePlayer = manager.getGamePlayer(player);
    if (gamePlayer instanceof Killer) {
      event.setDropItems(false);
      event.setExpToDrop(0);
      return;
    }

    final Block block = event.getBlock();
    final Material type = block.getType();
    final boolean canBreakBlock = this.whitelisted.contains(type);
    if (!canBreakBlock) {
      event.setCancelled(true);
    }
  }

  @EventHandler(priority = EventPriority.LOWEST)
  private void onBlockDamage(final BlockDamageEvent event) {

    final Player player = event.getPlayer();
    final PlayerManager manager = this.game.getPlayerManager();
    final boolean valid = manager.checkPlayerExists(player);
    if (!valid) {
      return;
    }

    final GamePlayer murderer = manager.getGamePlayer(player);
    if (murderer instanceof Survivor) {
      return;
    }

    final PlayerInventory inventory = player.getInventory();
    final ItemStack hand = inventory.getItemInMainHand();
    if (!PDCUtils.canBreakMapBlocks(hand)) {
      event.setCancelled(true);
      return;
    }

    final Block block = event.getBlock();
    final Material material = block.getType();
    if (material == Material.BEDROCK) {
      return;
    }

    event.setInstaBreak(true);
    final Location murdererLocation = murderer.getLocation();
    if (murderer instanceof Killer) {
      final SoundResource sound = Sounds.CHAINSAW;
      manager.stopSoundsForAllParticipants(sound);
      manager.playSoundForAllParticipantsAtLocation(murdererLocation, sound);
    } else {
      Item.builder(hand).useOneDurability();
    }
  }
}
