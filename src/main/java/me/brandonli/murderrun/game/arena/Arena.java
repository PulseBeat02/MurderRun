/*
 * This file is part of Murder Run, a spin-off game-mode of Dead by Daylight
 * Copyright (C) Brandon Li <https://brandonli.me/>
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program.  If not, see <https://www.gnu.org/licenses/>.
 */
package me.brandonli.murderrun.game.arena;

import static java.util.Objects.requireNonNull;

import jakarta.persistence.*;
import java.io.Serial;
import java.io.Serializable;
import java.util.UUID;
import me.brandonli.murderrun.data.hibernate.converters.LocationConverter;
import me.brandonli.murderrun.game.map.Schematic;
import me.brandonli.murderrun.utils.RandomUtils;
import me.brandonli.murderrun.utils.map.MapUtils;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.util.BoundingBox;

@Entity
@Table(name = "arena")
public final class Arena implements Serializable {

  @Serial
  private static final long serialVersionUID = -6251041532325023867L;

  @Id
  @GeneratedValue
  @Column(name = "id", updatable = false, nullable = false)
  private Long id;

  @Column(name = "schematic")
  @Lob
  private Schematic schematic;

  @Column(name = "name")
  private String name;

  @Convert(converter = LocationConverter.class)
  @Column(name = "corners")
  private Location[] corners;

  @Convert(converter = LocationConverter.class)
  @Column(name = "car_part_locations")
  private Location[] carPartLocations;

  @Convert(converter = LocationConverter.class)
  @Column(name = "spawn")
  private Location spawn;

  @Convert(converter = LocationConverter.class)
  @Column(name = "truck")
  private Location truck;

  public Arena() {}

  public Arena(
    final Schematic schematic,
    final String name,
    final Location[] corners,
    final Location[] carPartLocations,
    final Location spawn,
    final Location truck
  ) {
    this.schematic = schematic;
    this.name = name;
    this.corners = corners;
    this.carPartLocations = carPartLocations;
    this.spawn = spawn;
    this.truck = truck;
    this.schematic.loadSchematicIntoMemory();
  }

  public Arena(final Arena arena) {
    this.schematic = new Schematic(arena.schematic);
    this.name = arena.name;
    this.id = arena.id;
    this.corners = MapUtils.copyLocationArray(arena.corners);
    this.carPartLocations = MapUtils.copyLocationArray(arena.carPartLocations);
    this.spawn = arena.spawn.clone();
    this.truck = arena.truck.clone();
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

  public Schematic getSchematic() {
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
    final Location location = this.carPartLocations[index];
    final Location drop = location.clone();
    drop.add(0, 1.5, 0);
    return drop;
  }

  public Arena relativizeLocations(final UUID uuid) {
    final String name = uuid.toString();
    final World world = requireNonNull(Bukkit.getWorld(name));
    final Location[] newCorners = MapUtils.copyLocationArray(this.corners);
    final Location[] newCarPartLocations = MapUtils.copyLocationArray(this.carPartLocations);
    final Location newSpawn = this.spawn.clone();
    final Location newTruck = this.truck.clone();
    newCorners[0].setWorld(world);
    newCorners[1].setWorld(world);
    newSpawn.setWorld(world);
    newTruck.setWorld(world);
    for (final Location carPartLocation : newCarPartLocations) {
      carPartLocation.setWorld(world);
    }
    return new Arena(this.schematic, this.name, newCorners, newCarPartLocations, newSpawn, newTruck);
  }
}
