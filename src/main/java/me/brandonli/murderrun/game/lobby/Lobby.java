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
package me.brandonli.murderrun.game.lobby;

import static java.util.Objects.requireNonNull;

import jakarta.persistence.*;
import java.io.Serial;
import java.io.Serializable;
import java.util.UUID;
import me.brandonli.murderrun.data.hibernate.converters.LocationConverter;
import me.brandonli.murderrun.game.map.Schematic;
import me.brandonli.murderrun.utils.map.MapUtils;
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

  public Lobby(
      final Schematic schematic,
      final String name,
      final Location[] corners,
      final Location lobbySpawn) {
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
