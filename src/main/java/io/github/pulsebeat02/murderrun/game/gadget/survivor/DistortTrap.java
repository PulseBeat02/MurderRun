package io.github.pulsebeat02.murderrun.game.gadget.survivor;

import io.github.pulsebeat02.murderrun.game.Game;
import io.github.pulsebeat02.murderrun.game.player.GamePlayer;
import io.github.pulsebeat02.murderrun.game.scheduler.GameScheduler;
import io.github.pulsebeat02.murderrun.locale.Locale;
import java.awt.Color;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.Particle;

public final class DistortTrap extends SurvivorTrap {

  public DistortTrap() {
    super(
        "distort",
        Material.PRISMARINE_SHARD,
        Locale.DISTORT_TRAP_NAME.build(),
        Locale.DISTORT_TRAP_LORE.build(),
        Locale.DISTORT_TRAP_ACTIVATE.build(),
        new Color(177, 156, 217));
  }

  @Override
  public void onTrapActivate(final Game game, final GamePlayer murderer) {
    final GameScheduler scheduler = game.getScheduler();
    scheduler.scheduleRepeatedTask(() -> this.spawnParticle(murderer), 0, 5, 7 * 20);
  }

  private void spawnParticle(final GamePlayer murderer) {
    final Location dummy = murderer.getLocation();
    murderer.spawnParticle(Particle.ELDER_GUARDIAN, dummy, 1, 0, 0, 0);
  }
}
