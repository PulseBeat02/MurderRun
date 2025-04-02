/*

MIT License

Copyright (c) 2025 Brandon Li

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
