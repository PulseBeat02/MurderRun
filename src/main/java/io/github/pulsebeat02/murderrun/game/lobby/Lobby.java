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
package io.github.pulsebeat02.murderrun.game.lobby;

import static java.util.Objects.requireNonNull;

import io.github.pulsebeat02.murderrun.data.hibernate.converters.LocationConverter;
import io.github.pulsebeat02.murderrun.game.map.Schematic;
import io.github.pulsebeat02.murderrun.utils.map.MapUtils;
import jakarta.persistence.*;
import java.io.Serial;
import java.io.Serializable;
import java.util.UUID;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.World;

@Entity
@Table(name = "lobby")
public final class Lobby implements Serializable {

  @Serial
  private static final long serialVersionUID = -3340383856074756744L;

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
  @Column(name = "lobby_spawn")
  private Location lobbySpawn;

  @Convert(converter = LocationConverter.class)
  @Column(name = "corners")
  private Location[] corners;

  public Lobby() {}

  public Lobby(final Schematic schematic, final String name, final Location[] corners, final Location lobbySpawn) {
    this.schematic = schematic;
    this.name = name;
    this.corners = corners;
    this.lobbySpawn = lobbySpawn;
    this.schematic.loadSchematicIntoMemory();
  }

  public Lobby(final Lobby lobby) {
    this.id = lobby.id;
    this.schematic = new Schematic(lobby.schematic);
    this.name = lobby.name;
    this.corners = MapUtils.copyLocationArray(lobby.corners);
    this.lobbySpawn = lobby.lobbySpawn.clone();
  }

  public String getName() {
    return this.name;
  }

  public Schematic getSchematic() {
    return this.schematic;
  }

  public Location getLobbySpawn() {
    return this.lobbySpawn;
  }

  public Location[] getCorners() {
    return this.corners;
  }

  public Lobby relativizeLocations(final UUID uuid) {
    final String name = uuid.toString();
    final World world = requireNonNull(Bukkit.getWorld(name));
    final Location[] newCorners = MapUtils.copyLocationArray(this.corners);
    final Location newLobbySpawn = this.lobbySpawn.clone();
    newCorners[0].setWorld(world);
    newCorners[1].setWorld(world);
    newLobbySpawn.setWorld(world);
    return new Lobby(this.schematic, this.name, newCorners, newLobbySpawn);
  }
}
