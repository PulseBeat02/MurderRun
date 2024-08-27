package io.github.pulsebeat02.murderrun.game.gadget.survivor.trap;

import io.github.pulsebeat02.murderrun.game.Game;
import io.github.pulsebeat02.murderrun.game.player.GamePlayer;
import io.github.pulsebeat02.murderrun.game.player.PlayerManager;
import io.github.pulsebeat02.murderrun.game.scheduler.GameScheduler;
import io.github.pulsebeat02.murderrun.locale.Message;
import java.awt.Color;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.entity.Item;
import org.bukkit.util.Vector;

public final class NeckSnapTrap extends SurvivorTrap {

  private static final int NECK_SNAP_TRAP_DURATION = 7 * 20;
  private static final String NECK_SNAP_TRAP_SOUND = "block.glass.break";

  private static final Vector UP = new Vector(0, 1, 0);

  public NeckSnapTrap() {
    super(
        "neck_snap",
        Material.BONE,
        Message.NECK_SNAP_NAME.build(),
        Message.NECK_SNAP_LORE.build(),
        Message.NECK_SNAP_ACTIVATE.build(),
        16,
        Color.GREEN);
  }

  @Override
  public void onTrapActivate(final Game game, final GamePlayer murderer, final Item item) {
    final PlayerManager manager = game.getPlayerManager();
    final GameScheduler scheduler = game.getScheduler();
    scheduler.scheduleRepeatedTask(
        () -> this.setLookDirection(murderer), 0, 5, NECK_SNAP_TRAP_DURATION);
    manager.playSoundForAllParticipants(NECK_SNAP_TRAP_SOUND);
  }

  private void setLookDirection(final GamePlayer player) {
    final Location location = player.getLocation();
    location.setDirection(UP);
    player.teleport(location);
  }
}
