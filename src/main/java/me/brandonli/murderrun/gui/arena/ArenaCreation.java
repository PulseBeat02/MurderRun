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
package me.brandonli.murderrun.gui.arena;

import jakarta.persistence.*;
import java.io.Serial;
import java.io.Serializable;
import java.util.Collection;
import me.brandonli.murderrun.data.hibernate.converters.LocationConverter;
import org.bukkit.Location;

@Entity
@Table(name = "arena")
public final class ArenaCreation implements Serializable {

  @Serial
  private static final long serialVersionUID = -5175701778472967665L;

  @Id
  @GeneratedValue
  @Column(name = "id", updatable = false, nullable = false)
  private Long id;

  @Column(name = "arena_name")
  private volatile String arenaName;

  @Convert(converter = LocationConverter.class)
  @Column(name = "spawn")
  private volatile Location spawn;

  @Convert(converter = LocationConverter.class)
  @Column(name = "truck")
  private volatile Location truck;

  @Convert(converter = LocationConverter.class)
  @Column(name = "first")
  private volatile Location first;

  @Convert(converter = LocationConverter.class)
  @Column(name = "second")
  private volatile Location second;

  @Convert(converter = LocationConverter.class)
  @Column(name = "item_locations")
  private volatile Collection<Location> itemLocations;

  public ArenaCreation(
    final String arenaName,
    final Location spawn,
    final Location truck,
    final Location first,
    final Location second,
    final Collection<Location> itemLocations
  ) {
    this.arenaName = arenaName;
    this.spawn = spawn;
    this.truck = truck;
    this.first = first;
    this.second = second;
    this.itemLocations = itemLocations;
  }

  public ArenaCreation() {}

  public String getArenaName() {
    return this.arenaName;
  }

  public void setArenaName(final String arenaName) {
    this.arenaName = arenaName;
  }

  public Location getSpawn() {
    return this.spawn;
  }

  public void setSpawn(final Location spawn) {
    this.spawn = spawn;
  }

  public Location getTruck() {
    return this.truck;
  }

  public void setTruck(final Location truck) {
    this.truck = truck;
  }

  public Location getFirst() {
    return this.first;
  }

  public void setFirst(final Location first) {
    this.first = first;
  }

  public Location getSecond() {
    return this.second;
  }

  public void setSecond(final Location second) {
    this.second = second;
  }

  public Collection<Location> getItemLocations() {
    return this.itemLocations;
  }

  public void setItemLocations(final Collection<Location> itemLocations) {
    this.itemLocations = itemLocations;
  }
}
