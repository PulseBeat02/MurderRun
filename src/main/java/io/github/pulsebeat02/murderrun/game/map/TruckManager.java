package io.github.pulsebeat02.murderrun.game.map;

import static java.util.Objects.requireNonNull;

import io.github.pulsebeat02.murderrun.game.Game;
import io.github.pulsebeat02.murderrun.game.GameExecutors;
import io.github.pulsebeat02.murderrun.game.GameSettings;
import io.github.pulsebeat02.murderrun.game.arena.Arena;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;
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
    final GameExecutors provider = game.getExecutorProvider();
    final ScheduledExecutorService scheduled = provider.getScheduledExecutor();
    scheduled.scheduleAtFixedRate(this::spawnParticleOnTruck, 0, 500, TimeUnit.MILLISECONDS);
  }

  private void spawnParticleOnTruck() {
    final Game game = this.map.getGame();
    final GameSettings settings = game.getSettings();
    final Arena arena = requireNonNull(settings.getArena());
    final Location truck = arena.getTruck();
    final World world = requireNonNull(truck.getWorld());
    world.spawnParticle(Particle.LAVA, truck, 10, 0.5, 0.5, 0.5);
    world.spawnParticle(Particle.SMOKE, truck, 10, 0.5, 0.5, 0.5);
  }
}
