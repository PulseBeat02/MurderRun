package io.github.pulsebeat02.murderrun.game.map.part;

import static java.util.Objects.requireNonNull;

import io.github.pulsebeat02.murderrun.game.Game;
import io.github.pulsebeat02.murderrun.game.GameSettings;
import io.github.pulsebeat02.murderrun.game.arena.Arena;
import io.github.pulsebeat02.murderrun.game.map.Map;
import io.github.pulsebeat02.murderrun.utils.Keys;
import io.github.pulsebeat02.murderrun.utils.MapUtils;
import java.awt.*;
import java.util.HashMap;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;
import org.bukkit.Location;
import org.bukkit.Particle;
import org.bukkit.World;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.persistence.PersistentDataContainer;
import org.bukkit.persistence.PersistentDataType;
import org.checkerframework.checker.nullness.qual.Nullable;

public final class PartsManager {

  private final Map map;
  private final java.util.Map<String, CarPart> parts;
  private final ScheduledExecutorService service;

  public PartsManager(final Map map) {
    this.map = map;
    this.parts = new HashMap<>();
    this.service = Executors.newScheduledThreadPool(1);
  }

  public void spawnParts() {
    this.randomizeSpawnLocations();
    this.spawnParticles();
  }

  private void randomizeSpawnLocations() {
    final Game game = this.map.getGame();
    final GameSettings configuration = game.getSettings();
    final Arena arena = requireNonNull(configuration.getArena());
    final Location first = arena.getFirstCorner();
    final Location second = arena.getSecondCorner();
    final World world = first.getWorld();
    final int parts = configuration.getCarPartCount();
    for (int i = 0; i < parts; i++) {
      final double[] coords = MapUtils.generateFriendlyRandomXZ(first, second);
      final Location location = new Location(world, coords[0], 320, coords[1]);
      final CarPart part = new CarPart(location);
      final String id = part.getUuid();
      part.spawn();
      this.parts.put(id, part);
    }
  }

  private void spawnParticles() {
    this.service.scheduleAtFixedRate(
        () -> this.parts.values().stream()
            .filter(part -> !part.isPickedUp())
            .forEach(this::spawnParticleOnPart),
        0,
        1,
        TimeUnit.SECONDS);
  }

  private void spawnParticleOnPart(final CarPart stack) {
    final Location location = stack.getLocation();
    final Location clone = location.clone().add(0, 1, 0);
    final World world = requireNonNull(clone.getWorld());
    world.spawnParticle(Particle.DUST, clone, 10, 0.2, 0.2, 0.2, Color.YELLOW);
  }

  public void shutdownExecutor() {
    this.service.shutdown();
  }

  public Map getMap() {
    return this.map;
  }

  public java.util.Map<String, CarPart> getParts() {
    return this.parts;
  }

  public void removeCarPart(final CarPart stack) {
    final String uuid = stack.getUuid();
    this.parts.remove(uuid);
  }

  public @Nullable CarPart getCarPartItemStack(final ItemStack stack) {
    final ItemMeta meta = requireNonNull(stack.getItemMeta());
    final PersistentDataContainer container = meta.getPersistentDataContainer();
    final String uuid = container.get(Keys.CAR_PART_UUID, PersistentDataType.STRING);
    return uuid == null ? null : this.parts.get(uuid);
  }
}
