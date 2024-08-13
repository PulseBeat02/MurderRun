package io.github.pulsebeat02.murderrun.game.map.event;

import io.github.pulsebeat02.murderrun.game.Game;
import io.github.pulsebeat02.murderrun.game.player.GamePlayer;
import io.github.pulsebeat02.murderrun.game.player.Killer;
import io.github.pulsebeat02.murderrun.game.player.PlayerManager;
import io.github.pulsebeat02.murderrun.resourcepack.sound.SoundKeys;
import io.github.pulsebeat02.murderrun.utils.ItemUtils;
import java.util.Optional;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.PlayerInventory;

public final class GamePlayerBlockBreakEvent implements Listener {

  private final Game game;

  public GamePlayerBlockBreakEvent(final Game game) {
    this.game = game;
  }

  public Game getGame() {
    return this.game;
  }

  @EventHandler(priority = EventPriority.LOWEST)
  private void onBlockBreakEvent(final BlockBreakEvent event) {

    final Player player = event.getPlayer();
    final PlayerInventory inventory = player.getInventory();
    final ItemStack hand = inventory.getItemInMainHand();
    if (!ItemUtils.canBreakMapBlocks(hand)) {
      return;
    }

    final Optional<GamePlayer> optional = this.game.checkIfValidEventPlayer(player);
    if (optional.isEmpty()) {
      return;
    }

    final Block block = event.getBlock();
    final Material material = block.getType();
    if (material == Material.BEDROCK) {
      return;
    }

    final GamePlayer murderer = optional.get();
    final Location murdererLocation = murderer.getLocation();
    final PlayerManager manager = this.game.getPlayerManager();
    if (murderer instanceof Killer) {
      manager.playSoundForAllParticipantsAtLocation(murdererLocation, SoundKeys.CHAINSAW);
    }

    event.setCancelled(true);
    block.breakNaturally();
  }
}
