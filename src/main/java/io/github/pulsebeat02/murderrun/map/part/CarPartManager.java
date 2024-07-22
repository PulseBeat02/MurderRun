package io.github.pulsebeat02.murderrun.map.part;

import io.github.pulsebeat02.murderrun.MurderGame;
import io.github.pulsebeat02.murderrun.config.GameConfiguration;
import io.github.pulsebeat02.murderrun.map.MurderMap;
import org.bukkit.Location;
import org.bukkit.World;

import java.util.Collection;
import java.util.HashSet;
import java.util.SplittableRandom;

public final class CarPartManager {

  private final MurderMap map;
  private final Collection<CarPart> parts;

  public CarPartManager(final MurderMap map) {
    this.map = map;
    this.parts = new HashSet<>();
  }

  public void spawnParts() {
    final MurderGame game = this.map.getGame();
    final GameConfiguration configuration = game.getConfiguration();
    final int parts = configuration.getCarPartCount();
    final SplittableRandom random = new SplittableRandom();
    final Location first = configuration.getFirstCorner();
    final Location second = configuration.getSecondCorner();
    final World world = first.getWorld();
    for (int i = 0; i < parts; i++) {
      final double x = first.getX() + (second.getX() - first.getX()) * random.nextDouble();
      final double y = first.getY() + (second.getY() - first.getY()) * random.nextDouble();
      final double z = first.getZ() + (second.getZ() - first.getZ()) * random.nextDouble();
      final Location location = new Location(world, x, y, z);
      final CarPart part = new CarPart(location);
      this.parts.add(part);
    }
  }

  public MurderMap getMap() {
    return this.map;
  }

  public Collection<CarPart> getParts() {
    return this.parts;
  }
}
