package io.github.pulsebeat02.murderrun.game.gadget.killer.utility;

import io.github.pulsebeat02.murderrun.game.Game;
import io.github.pulsebeat02.murderrun.game.gadget.killer.KillerGadget;
import io.github.pulsebeat02.murderrun.game.player.GamePlayer;
import io.github.pulsebeat02.murderrun.game.player.PlayerManager;
import io.github.pulsebeat02.murderrun.game.scheduler.GameScheduler;
import io.github.pulsebeat02.murderrun.locale.Message;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.block.BlockFace;
import org.bukkit.entity.Player;
import org.bukkit.event.player.PlayerDropItemEvent;

public final class IcePath extends KillerGadget {

  public IcePath() {
    super(
        "ice_path", Material.ICE, Message.ICE_PATH_NAME.build(), Message.ICE_PATH_LORE.build(), 32);
  }

  @Override
  public void onGadgetDrop(final Game game, final PlayerDropItemEvent event, final boolean remove) {
    super.onGadgetDrop(game, event, true);
    final Player player = event.getPlayer();
    final GameScheduler scheduler = game.getScheduler();
    scheduler.scheduleRepeatedTask(() -> this.setIceTrail(player), 0, 4);
    final PlayerManager manager = game.getPlayerManager();
    final GamePlayer gamePlayer = manager.getGamePlayer(player);
    gamePlayer.playSound("block.glass.break");
  }

  private void setIceTrail(final Player player) {
    final Location location = player.getLocation();
    final Block block = location.getBlock();
    final Block lower = block.getRelative(BlockFace.DOWN);
    lower.setType(Material.ICE);
  }
}
