package io.github.pulsebeat02.murderrun.game.gadget.killer.utility;

import io.github.pulsebeat02.murderrun.game.Game;
import io.github.pulsebeat02.murderrun.game.gadget.GameProperties;
import io.github.pulsebeat02.murderrun.game.gadget.killer.KillerGadget;
import io.github.pulsebeat02.murderrun.game.player.GamePlayer;
import io.github.pulsebeat02.murderrun.game.player.PlayerAudience;
import io.github.pulsebeat02.murderrun.game.scheduler.GameScheduler;
import io.github.pulsebeat02.murderrun.locale.Message;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.block.BlockFace;
import org.bukkit.entity.Item;

public final class IcePath extends KillerGadget {

  public IcePath() {
    super(
        "ice_path",
        Material.ICE,
        Message.ICE_PATH_NAME.build(),
        Message.ICE_PATH_LORE.build(),
        GameProperties.ICE_PATH_COST);
  }

  @Override
  public boolean onGadgetDrop(
      final Game game, final GamePlayer player, final Item item, final boolean remove) {

    super.onGadgetDrop(game, player, item, true);

    final GameScheduler scheduler = game.getScheduler();
    scheduler.scheduleRepeatedTask(() -> this.setIceTrail(player), 0, 4);

    final PlayerAudience audience = player.getAudience();
    audience.playSound(GameProperties.ICE_PATH_SOUND);

    return false;
  }

  private void setIceTrail(final GamePlayer player) {
    final Location location = player.getLocation();
    final Block block = location.getBlock();
    final Block lower = block.getRelative(BlockFace.DOWN);
    lower.setType(Material.ICE);
  }
}
