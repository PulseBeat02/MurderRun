package io.github.pulsebeat02.murderrun.game.map;

import static java.util.Objects.requireNonNull;

import io.github.pulsebeat02.murderrun.game.Game;
import io.github.pulsebeat02.murderrun.game.GameSettings;
import io.github.pulsebeat02.murderrun.game.arena.Arena;
import io.github.pulsebeat02.murderrun.game.scheduler.GameScheduler;
import org.bukkit.Location;
import org.bukkit.Particle;
import org.bukkit.World;

public final class TruckManager {

  private final Map map;

  public TruckManager(final Map map) {
    this.map = map;
  }

  public void spawnParticles() {
    final Game game = this.map.getGame();
    final GameScheduler scheduler = game.getScheduler();
    scheduler.scheduleRepeatedTask(this::spawnParticleOnTruck, 0, 4L);
  }

  private void spawnParticleOnTruck() {
    final Game game = this.map.getGame();
    final GameSettings settings = game.getSettings();
    final Arena arena = requireNonNull(settings.getArena());
    final Location truck = arena.getTruck();
    final World world = requireNonNull(truck.getWorld());
    world.spawnParticle(Particle.LAVA, truck, 3, 0.5, 0.5, 0.5);
    world.spawnParticle(Particle.SMOKE, truck, 3, 0.5, 0.5, 0.5);
  }
}
