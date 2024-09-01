package io.github.pulsebeat02.murderrun.game.arena;

import io.github.pulsebeat02.murderrun.utils.RandomUtils;
import org.bukkit.Location;
import org.bukkit.util.BoundingBox;

public final class Arena {

  private final ArenaSchematic schematic;
  private final String name;
  private final Location[] corners;
  private final Location[] carPartLocations;
  private final Location spawn;
  private final Location truck;

  public Arena(
      final ArenaSchematic schematic,
      final String name,
      final Location[] corners,
      final Location[] carPartLocations,
      final Location spawn,
      final Location truck) {
    this.schematic = schematic;
    this.name = name;
    this.corners = corners;
    this.carPartLocations = carPartLocations;
    this.spawn = spawn;
    this.truck = truck;
    this.checkArray();
  }

  private void checkArray() {
    final int length = this.corners.length;
    if (length < 2) {
      throw new AssertionError("Not enough corners! Two required for map");
    }
  }

  public String getName() {
    return this.name;
  }

  public Location getFirstCorner() {
    return this.corners[0];
  }

  public Location getSecondCorner() {
    return this.corners[1];
  }

  public Location getSpawn() {
    return this.spawn;
  }

  public Location getTruck() {
    return this.truck;
  }

  public Location[] getCorners() {
    return this.corners;
  }

  public ArenaSchematic getSchematic() {
    return this.schematic;
  }

  public BoundingBox createBox() {
    return BoundingBox.of(this.corners[0], this.corners[1]);
  }

  public Location[] getCarPartLocations() {
    return this.carPartLocations;
  }

  public Location getRandomItemLocation() {

    final int length = this.carPartLocations.length;
    if (length == 0) {
      return this.spawn;
    }

    final int index = RandomUtils.generateInt(length);
    return this.carPartLocations[index];
  }
}
