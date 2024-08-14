package io.github.pulsebeat02.murderrun.game.gadget.survivor;

import io.github.pulsebeat02.murderrun.game.Game;
import io.github.pulsebeat02.murderrun.game.player.GamePlayer;
import io.github.pulsebeat02.murderrun.game.scheduler.GameScheduler;
import io.github.pulsebeat02.murderrun.locale.Locale;
import java.awt.Color;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.util.Vector;

public final class NeckSnapTrap extends SurvivorTrap {

  private static final Vector UP = new Vector(0, 1, 0);

  public NeckSnapTrap() {
    super(
        "neck_snap",
        Material.BONE,
        Locale.NECK_SNAP_TRAP_NAME.build(),
        Locale.NECK_SNAP_TRAP_LORE.build(),
        Locale.NECK_SNAP_TRAP_ACTIVATE.build(),
        16,
        Color.GREEN);
  }

  @Override
  public void onTrapActivate(final Game game, final GamePlayer murderer) {
    final GameScheduler scheduler = game.getScheduler();
    scheduler.scheduleRepeatedTask(() -> this.setHeadUp(murderer), 0, 5, 7 * 20);
  }

  private void setHeadUp(final GamePlayer player) {
    final Location location = player.getLocation();
    location.setDirection(UP);
    player.teleport(location);
  }
}
