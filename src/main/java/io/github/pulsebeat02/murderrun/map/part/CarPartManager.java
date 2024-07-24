package io.github.pulsebeat02.murderrun.map.part;

import io.github.pulsebeat02.murderrun.arena.MurderArena;
import io.github.pulsebeat02.murderrun.game.MurderGame;
import io.github.pulsebeat02.murderrun.game.MurderSettings;
import io.github.pulsebeat02.murderrun.map.MurderMap;
import io.github.pulsebeat02.murderrun.utils.MapUtils;
import org.bukkit.Location;
import org.bukkit.NamespacedKey;
import org.bukkit.Particle;
import org.bukkit.World;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.persistence.PersistentDataContainer;
import org.bukkit.persistence.PersistentDataType;

import java.awt.*;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

public final class CarPartManager {

  private final MurderMap map;
  private final Map<String, CarPartItemStack> parts;
  private final ScheduledExecutorService service;

  public CarPartManager(final MurderMap map) {
    this.map = map;
    this.parts = new HashMap<>();
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
    final MurderSettings configuration = game.getSettings();
    final MurderArena arena = configuration.getArena();
    final Location first = arena.getFirstCorner();
    final Location second = arena.getSecondCorner();
    final World world = first.getWorld();
    final int parts = configuration.getCarPartCount();
    for (int i = 0; i < parts; i++) {
      final double[] coords = MapUtils.generateFriendlyRandomXZ(first, second);
      final Location location = new Location(world, coords[0], 320, coords[1]);
      final CarPartItemStack part = new CarPartItemStack(location);
      final String id = part.getUuid();
      part.spawn();
      this.parts.put(id, part);
    }
  }

  private void spawnParticles() {
    this.service.scheduleAtFixedRate(
        () ->
            this.parts.values().stream()
                .filter(part -> !part.isPickedUp())
                .forEach(this::spawnParticleOnPart),
        0,
        1,
        TimeUnit.SECONDS);
  }

  private void spawnParticleOnPart(final CarPartItemStack stack) {
    final Location location = stack.getLocation();
    final Location clone = location.clone().add(0, 1, 0);
    final World world = clone.getWorld();
    world.spawnParticle(Particle.REDSTONE, clone, 10, 0.5, 0.5, 0.5, Color.YELLOW);
  }

  public MurderMap getMap() {
    return this.map;
  }

  public Map<String, CarPartItemStack> getParts() {
    return this.parts;
  }

  public void removeCarPart(final CarPartItemStack stack) {
    final String uuid = stack.getUuid();
    this.parts.remove(uuid);
  }

  public CarPartItemStack getCarPartItemStack(final ItemStack stack) {
    final ItemMeta meta = stack.getItemMeta();
    final PersistentDataContainer container = meta.getPersistentDataContainer();
    final NamespacedKey key = CarPartItemStack.getCarPartKey();
    final String uuid = container.get(key, PersistentDataType.STRING);
    return this.parts.get(uuid);
  }
}
