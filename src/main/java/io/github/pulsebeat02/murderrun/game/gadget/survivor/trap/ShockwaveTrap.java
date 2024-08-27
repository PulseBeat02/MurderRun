package io.github.pulsebeat02.murderrun.game.gadget.survivor.trap;

import static java.util.Objects.requireNonNull;

import io.github.pulsebeat02.murderrun.game.Game;
import io.github.pulsebeat02.murderrun.game.player.GamePlayer;
import io.github.pulsebeat02.murderrun.game.player.Participant;
import io.github.pulsebeat02.murderrun.game.player.PlayerManager;
import io.github.pulsebeat02.murderrun.locale.Message;
import java.awt.Color;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.World;
import org.bukkit.entity.Item;
import org.bukkit.util.Vector;

public final class ShockwaveTrap extends SurvivorTrap {

  private static final double SHOCKWAVE_TRAP_EXPLOSION_RADIUS = 10D;
  private static final double SHOCKWAVE_TRAP_EXPLOSION_POWER = 2D;
  private static final String SHOCKWAVE_TRAP_SOUND = "entity.creeper.death";

  public ShockwaveTrap() {
    super(
        "shockwave",
        Material.TNT,
        Message.SHOCKWAVE_NAME.build(),
        Message.SHOCKWAVE_LORE.build(),
        Message.SHOCKWAVE_ACTIVATE.build(),
        32,
        new Color(255, 215, 0));
  }

  @Override
  public void onTrapActivate(final Game game, final GamePlayer survivor, final Item item) {

    final Location origin = item.getLocation();
    final World world = requireNonNull(origin.getWorld());
    world.createExplosion(origin, 0, false, false);

    final PlayerManager manager = game.getPlayerManager();
    manager.applyToAllParticipants(participant -> this.applyShockwave(participant, origin));
    manager.playSoundForAllParticipants(SHOCKWAVE_TRAP_SOUND);
  }

  private void applyShockwave(final Participant participant, final Location origin) {
    final Location location = participant.getLocation();
    final double distance = location.distanceSquared(origin);
    if (distance < SHOCKWAVE_TRAP_EXPLOSION_RADIUS * SHOCKWAVE_TRAP_EXPLOSION_RADIUS) {
      final Vector playerVector = location.toVector();
      final Vector blockVector = origin.toVector();
      final Vector vector = playerVector.subtract(blockVector);
      vector.normalize();
      vector.multiply(SHOCKWAVE_TRAP_EXPLOSION_POWER);
      participant.setVelocity(vector);
    }
  }
}
