package io.github.pulsebeat02.murderrun.map.part;

import io.github.pulsebeat02.murderrun.arena.MurderArena;
import io.github.pulsebeat02.murderrun.game.MurderGame;
import io.github.pulsebeat02.murderrun.game.GameSettings;
import io.github.pulsebeat02.murderrun.map.MurderMap;
import io.github.pulsebeat02.murderrun.utils.MapUtils;
import org.bukkit.Location;
import org.bukkit.Particle;
import org.bukkit.World;

import java.awt.*;
import java.util.Collection;
import java.util.HashSet;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

public final class CarPartManager {

  private final MurderMap map;
  private final Collection<CarPartItemStack> parts;
  private final ScheduledExecutorService service;

  public CarPartManager(final MurderMap map) {
    this.map = map;
    this.parts = new HashSet<>();
    this.service = Executors.newScheduledThreadPool(1);
  }

  public void spawnParts() {
    this.randomizeSpawnLocations();
    this.spawnParticles();
  }

  public void shutdownExecutor() {
    this.service.shutdown();
  }

  private void randomizeSpawnLocations() {
    final MurderGame game = this.map.getGame();
    final GameSettings configuration = game.getSettings();
    final MurderArena arena = configuration.getArena();
    final Location first = arena.getFirstCorner();
    final Location second = arena.getSecondCorner();
    final World world = first.getWorld();
    final int parts = configuration.getCarPartCount();
    for (int i = 0; i < parts; i++) {
      final double[] coords = MapUtils.generateFriendlyRandomXZ(first, second);
      final Location location = new Location(world, coords[0], 320, coords[1]);
      final CarPartItemStack part = new CarPartItemStack(location);
      part.spawn();
      this.parts.add(part);
    }
  }

  private void spawnParticles() {
    this.service.scheduleAtFixedRate(
        () -> this.parts.forEach(this::spawnParticleOnPart), 0, 1, TimeUnit.SECONDS);
  }


  // TODO: fix it will keep spawning particles after picked up
  private void spawnParticleOnPart(final CarPartItemStack stack) {
    final Location location = stack.getLocation();
    final Location clone = location.clone().add(0, 1, 0);
    final World world = clone.getWorld();
    world.spawnParticle(Particle.ENTITY_EFFECT, clone, 10, 0.5, 0.5, 0.5, Color.YELLOW);
  }

  public MurderMap getMap() {
    return this.map;
  }

  public Collection<CarPartItemStack> getParts() {
    return this.parts;
  }
}
