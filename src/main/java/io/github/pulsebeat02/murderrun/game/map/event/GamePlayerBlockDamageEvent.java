package io.github.pulsebeat02.murderrun.game.map.event;

import io.github.pulsebeat02.murderrun.game.Game;
import io.github.pulsebeat02.murderrun.game.player.GamePlayer;
import io.github.pulsebeat02.murderrun.game.player.Killer;
import io.github.pulsebeat02.murderrun.game.player.PlayerManager;
import io.github.pulsebeat02.murderrun.resourcepack.sound.SoundResource;
import io.github.pulsebeat02.murderrun.resourcepack.sound.Sounds;
import io.github.pulsebeat02.murderrun.utils.ItemUtils;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.block.BlockDamageEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.PlayerInventory;

public final class GamePlayerBlockDamageEvent implements Listener {

  private final Game game;

  public GamePlayerBlockDamageEvent(final Game game) {
    this.game = game;
  }

  public Game getGame() {
    return this.game;
  }

  @EventHandler(priority = EventPriority.LOWEST)
  private void onBlockDamage(final BlockDamageEvent event) {

    final Player player = event.getPlayer();
    final PlayerInventory inventory = player.getInventory();
    final ItemStack hand = inventory.getItemInMainHand();
    if (!ItemUtils.canBreakMapBlocks(hand)) {
      return;
    }

    final PlayerManager manager = this.game.getPlayerManager();
    final boolean valid = manager.checkPlayerExists(player);
    if (!valid) {
      return;
    }

    final Block block = event.getBlock();
    final Material material = block.getType();
    if (material == Material.BEDROCK) {
      return;
    }

    final GamePlayer murderer = manager.getGamePlayer(player);
    final Location murdererLocation = murderer.getLocation();
    if (murderer instanceof Killer) {
      final SoundResource sound = Sounds.CHAINSAW;
      manager.stopSoundsForAllParticipants(sound);
      manager.playSoundForAllParticipantsAtLocation(murdererLocation, sound);
    }

    event.setCancelled(true);
    block.setType(Material.AIR);
  }
}
