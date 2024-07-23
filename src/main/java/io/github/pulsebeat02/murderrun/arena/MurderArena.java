package io.github.pulsebeat02.murderrun.arena;

import org.bukkit.Location;

import java.nio.file.Path;

public final class MurderArena {

  private final MurderArenaSchematic schematic;
  private final String name;
  private final Location[] corners;
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
