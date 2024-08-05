package io.github.pulsebeat02.murderrun.game.arena;

import org.bukkit.Location;
import org.checkerframework.common.value.qual.MinLen;

public final class MurderArena {

  private final MurderArenaSchematic schematic;
  private final String name;
  private final @MinLen(2) Location[] corners;
  private final Location spawn;
  private final Location truck;

  public MurderArena(
      final MurderArenaSchematic schematic,
      final String name,
      final Location[] corners,
      final Location spawn,
      final Location truck) {
    this.schematic = schematic;
    this.name = name;
    this.corners = corners;
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

  public MurderArenaSchematic getSchematic() {
    return this.schematic;
  }
}
