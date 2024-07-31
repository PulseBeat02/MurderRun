package io.github.pulsebeat02.murderrun.gadget.innocent;

import io.github.pulsebeat02.murderrun.gadget.SurvivorTrap;
import io.github.pulsebeat02.murderrun.game.MurderGame;
import io.github.pulsebeat02.murderrun.locale.Locale;
import io.github.pulsebeat02.murderrun.player.GamePlayer;
import io.github.pulsebeat02.murderrun.scheduler.MurderGameScheduler;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.Particle;
import org.bukkit.World;
import org.bukkit.entity.Player;

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
    final Player player = murderer.getPlayer();
    final Location location = murderer.getLocation();
    location.add(0, 10, 0);
    player.setGravity(false);
    player.teleport(location);
    final MurderGameScheduler scheduler = game.getScheduler();
    scheduler.scheduleTask(() -> this.teleportBack(player, location), 20 * 7);
    scheduler.scheduleRepeatingTaskDuration(
        () -> this.spawnPortalParticles(location), 0, 10, 20 * 7);
  }

  private void spawnPortalParticles(final Location location) {
    final World world = location.getWorld();
    if (world == null) {
      throw new AssertionError("Failed to spawn portal particles!");
    }
    world.spawnParticle(Particle.DRAGON_BREATH, location, 10, 0.5, 0.5, 0.5);
  }

  private void teleportBack(final Player player, final Location clone) {
    player.teleport(clone);
    player.setGravity(false);
  }
}
