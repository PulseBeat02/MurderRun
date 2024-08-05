package io.github.pulsebeat02.murderrun.game.gadget.innocent;

import io.github.pulsebeat02.murderrun.game.MurderGame;
import io.github.pulsebeat02.murderrun.game.gadget.SurvivorTrap;
import io.github.pulsebeat02.murderrun.game.player.GamePlayer;
import io.github.pulsebeat02.murderrun.game.scheduler.MurderGameScheduler;
import io.github.pulsebeat02.murderrun.locale.Locale;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.Particle;
import org.bukkit.World;

public final class LevitationTrap extends SurvivorTrap {

  public LevitationTrap() {
    super(
        "levitation",
        Material.SHULKER_SHELL,
        Locale.LEVITATION_TRAP_NAME.build(),
        Locale.LEVITATION_TRAP_LORE.build(),
        Locale.LEVITATION_TRAP_ACTIVATE.build());
  }

  @Override
  public void onTrapActivate(final MurderGame game, final GamePlayer murderer) {

    super.onTrapActivate(game, murderer);

    final Location location = murderer.getLocation();
    location.add(0, 10, 0);

    final World world = location.getWorld();
    if (world == null) {
      throw new AssertionError("Failed to spawn portal particles!");
    }

    final MurderGameScheduler scheduler = game.getScheduler();
    this.setLevitation(murderer, location);
    scheduler.scheduleTask(() -> this.teleportBack(murderer, location), 20 * 7);
    scheduler.scheduleRepeatedTask(() -> this.spawnParticle(world, location), 0, 10, 20 * 7);
  }

  private void setLevitation(final GamePlayer murderer, final Location location) {
    murderer.apply(player -> {
      player.setGravity(false);
      player.teleport(location);
    });
  }

  private void spawnParticle(final World world, final Location location) {
    world.spawnParticle(Particle.DRAGON_BREATH, location, 10, 0.5, 0.5, 0.5);
  }

  private void teleportBack(final GamePlayer murderer, final Location clone) {
    murderer.apply(player -> {
      player.teleport(clone);
      player.setGravity(false);
    });
  }
}
