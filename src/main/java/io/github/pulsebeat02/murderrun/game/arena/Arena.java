/*

MIT License

Copyright (c) 2024 Brandon Li

Permission is hereby granted, free of charge, to any person obtaining a copy
of this software and associated documentation files (the "Software"), to deal
in the Software without restriction, including without limitation the rights
to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
copies of the Software, and to permit persons to whom the Software is
furnished to do so, subject to the following conditions:

The above copyright notice and this permission notice shall be included in all
copies or substantial portions of the Software.

THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE
SOFTWARE.

*/
package io.github.pulsebeat02.murderrun.game.arena;

import static java.util.Objects.requireNonNull;

import io.github.pulsebeat02.murderrun.data.hibernate.converters.LocationConverter;
import io.github.pulsebeat02.murderrun.game.map.Schematic;
import io.github.pulsebeat02.murderrun.utils.MapUtils;
import io.github.pulsebeat02.murderrun.utils.RandomUtils;
import jakarta.persistence.*;
import java.io.Serial;
import java.io.Serializable;
import java.util.UUID;
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
    this.schematic = arena.schematic;
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

  public void relativizeLocations(final UUID uuid) {
    final String name = uuid.toString();
    final World world = requireNonNull(Bukkit.getWorld(name));
    this.corners[0].setWorld(world);
    this.corners[1].setWorld(world);
    this.spawn.setWorld(world);
    this.truck.setWorld(world);
    for (final Location carPartLocation : this.carPartLocations) {
      carPartLocation.setWorld(world);
    }
  }
}
