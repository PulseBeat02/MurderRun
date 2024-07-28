package io.github.pulsebeat02.murderrun.map;

import io.github.pulsebeat02.murderrun.arena.MurderArena;
import io.github.pulsebeat02.murderrun.game.MurderGame;
import io.github.pulsebeat02.murderrun.game.MurderSettings;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;
import org.bukkit.Location;
import org.bukkit.Particle;
import org.bukkit.World;

public final class TruckManager {

  private final MurderMap map;
  private final ScheduledExecutorService service;

  public TruckManager(final MurderMap map) {
    this.map = map;
    this.service = Executors.newScheduledThreadPool(1);
  }

  public void spawnParticles() {
    this.service.scheduleAtFixedRate(this::spawnParticleOnTruck, 0, 500, TimeUnit.MILLISECONDS);
  }

  private void spawnParticleOnTruck() {
    final MurderGame game = this.map.getGame();
    final MurderSettings settings = game.getSettings();
    final MurderArena arena = settings.getArena();
    final Location truck = arena.getTruck();
    final World world = truck.getWorld();
    if (world == null) {
      throw new AssertionError("Location doesn't have World attached to it!");
    }
    world.spawnParticle(Particle.LAVA, truck, 10, 0.5, 0.5, 0.5);
  }

  public void shutdownExecutor() {
    this.service.shutdown();
  }
}
