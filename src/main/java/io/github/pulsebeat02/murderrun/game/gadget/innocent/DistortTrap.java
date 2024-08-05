package io.github.pulsebeat02.murderrun.game.gadget.innocent;

import io.github.pulsebeat02.murderrun.game.MurderGame;
import io.github.pulsebeat02.murderrun.game.gadget.SurvivorTrap;
import io.github.pulsebeat02.murderrun.game.player.GamePlayer;
import io.github.pulsebeat02.murderrun.game.scheduler.MurderGameScheduler;
import io.github.pulsebeat02.murderrun.locale.Locale;
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
        Locale.DISTORT_TRAP_ACTIVATE.build());
  }

  @Override
  public void onTrapActivate(final MurderGame game, final GamePlayer murderer) {

    super.onTrapActivate(game, murderer);

    final MurderGameScheduler scheduler = game.getScheduler();
    scheduler.scheduleRepeatedTask(() -> this.spawnParticle(murderer), 0, 5, 7 * 20);
  }

  private void spawnParticle(final GamePlayer murderer) {
    final Location dummy = murderer.getLocation();
    murderer.spawnParticle(Particle.ELDER_GUARDIAN, dummy, 1, 0, 0, 0);
  }
}
