package io.github.pulsebeat02.murderrun.game.gadget.killer.utility;

import io.github.pulsebeat02.murderrun.game.Game;
import io.github.pulsebeat02.murderrun.game.gadget.killer.KillerGadget;
import io.github.pulsebeat02.murderrun.game.scheduler.GameScheduler;
import io.github.pulsebeat02.murderrun.locale.Locale;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.entity.Player;
import org.bukkit.event.player.PlayerDropItemEvent;

public final class IcePath extends KillerGadget {

  public IcePath() {
    super(
        "ice_path",
        Material.ICE,
        Locale.ICE_PATH_TRAP_NAME.build(),
        Locale.ICE_PATH_TRAP_LORE.build(),
        32);
  }

  @Override
  public void onGadgetDrop(final Game game, final PlayerDropItemEvent event, final boolean remove) {
    super.onGadgetDrop(game, event, true);
    final Player player = event.getPlayer();
    final GameScheduler scheduler = game.getScheduler();
    scheduler.scheduleRepeatedTask(
        () -> {
          final Location location = player.getLocation();
          final Block block = location.getBlock();
          block.setType(Material.ICE);
        },
        0,
        10);
  }
}
