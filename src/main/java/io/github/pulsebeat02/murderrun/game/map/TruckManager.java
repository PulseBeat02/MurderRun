package io.github.pulsebeat02.murderrun.game.map;

import io.github.pulsebeat02.murderrun.game.Game;
import io.github.pulsebeat02.murderrun.game.GameSettings;
import io.github.pulsebeat02.murderrun.game.arena.Arena;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;
import org.bukkit.Location;
import org.bukkit.Particle;
import org.bukkit.World;

public final class TruckManager {

  private final Map map;
  private final ScheduledExecutorService service;

  public TruckManager(final Map map) {
    this.map = map;
    this.service = Executors.newScheduledThreadPool(1);
  }

  public void spawnParticles() {
    this.service.scheduleAtFixedRate(this::spawnParticleOnTruck, 0, 500, TimeUnit.MILLISECONDS);
  }

  private void spawnParticleOnTruck() {
    final Game game = this.map.getGame();
    final GameSettings settings = game.getSettings();
    final Arena arena = settings.getArena();
    final Location truck = arena.getTruck();
    final World world = truck.getWorld();
    if (world == null) {
      throw new AssertionError("Location doesn't have World attached to it!");
    }
    world.spawnParticle(Particle.LAVA, truck, 10, 0.5, 0.5, 0.5);
    world.spawnParticle(Particle.SMOKE, truck, 10, 0.5, 0.5, 0.5);
  }

  public void shutdownExecutor() {
    this.service.shutdown();
  }
}
