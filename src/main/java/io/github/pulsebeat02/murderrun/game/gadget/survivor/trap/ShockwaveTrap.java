package io.github.pulsebeat02.murderrun.game.gadget.survivor.trap;

import static java.util.Objects.requireNonNull;

import io.github.pulsebeat02.murderrun.game.Game;
import io.github.pulsebeat02.murderrun.game.gadget.GadgetSettings;
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

  public ShockwaveTrap() {
    super(
        "shockwave",
        Material.TNT,
        Message.SHOCKWAVE_NAME.build(),
        Message.SHOCKWAVE_LORE.build(),
        Message.SHOCKWAVE_ACTIVATE.build(),
        GadgetSettings.SHOCKWAVE_COST,
        new Color(255, 215, 0));
  }

  @Override
  public void onTrapActivate(final Game game, final GamePlayer survivor, final Item item) {

    final Location origin = item.getLocation();
    final World world = requireNonNull(origin.getWorld());
    world.createExplosion(origin, 0, false, false);

    final PlayerManager manager = game.getPlayerManager();
    manager.applyToAllParticipants(participant -> this.applyShockwave(participant, origin));
    manager.playSoundForAllParticipants(GadgetSettings.SHOCKWAVE_SOUND);
  }

  private void applyShockwave(final Participant participant, final Location origin) {
    final Location location = participant.getLocation();
    final double distance = location.distanceSquared(origin);
    final double radius = GadgetSettings.SHOCKWAVE_EXPLOSION_RADIUS;
    if (distance < radius * radius) {
      final Vector playerVector = location.toVector();
      final Vector blockVector = origin.toVector();
      final Vector vector = playerVector.subtract(blockVector);
      vector.normalize();
      vector.multiply(GadgetSettings.SHOCKWAVE_EXPLOSION_POWER);
      participant.setVelocity(vector);
    }
  }
}
