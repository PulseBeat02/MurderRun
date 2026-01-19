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

import jakarta.persistence.*;
import java.io.Serial;
import java.io.Serializable;
import java.nio.file.Path;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;
import me.brandonli.murderrun.api.event.ApiEventBus;
import me.brandonli.murderrun.api.event.EventBusProvider;
import me.brandonli.murderrun.api.event.contract.arena.ArenaEvent;
import me.brandonli.murderrun.api.event.contract.arena.ArenaModificationType;
import me.brandonli.murderrun.data.hibernate.identifier.HibernateSerializable;
import me.brandonli.murderrun.game.map.Schematic;
import me.brandonli.murderrun.utils.IOUtils;
import org.bukkit.Location;
import org.checkerframework.checker.nullness.qual.KeyFor;
import org.checkerframework.checker.nullness.qual.Nullable;

@Entity
@Table(name = "arena_manager")
public final class ArenaManager implements Serializable, HibernateSerializable {

  @Serial
  private static final long serialVersionUID = -2378194945450834205L;

  @Id
  @GeneratedValue
  @Column(name = "id", updatable = false, nullable = false)
  private Long id;

  @OneToMany(orphanRemoval = true, cascade = CascadeType.ALL)
  @MapKeyColumn(name = "name")
  @JoinColumn(name = "arena_manager_id")
  @Column(name = "arena")
  private final Map<String, Arena> arenas;

  public ArenaManager() {
    this.arenas = new HashMap<>();
  }

  public synchronized void addInternalArena(final Arena arena) {
    final ApiEventBus bus = EventBusProvider.getBus();
    if (bus.post(ArenaEvent.class, arena, ArenaModificationType.CREATION)) {
      return;
    }
    final String name = arena.getName();
    this.arenas.put(name, arena);
  }

  public synchronized void addArena(
      final String name,
      final Location[] corners,
      final Location[] itemLocations,
      final Location spawn,
      final Location truck) {
    final ApiEventBus bus = EventBusProvider.getBus();
    final Schematic schematic = Schematic.copyAndCreateSchematic(name, corners, true);
    final Arena arena = new Arena(schematic, name, corners, itemLocations, spawn, truck);
    if (bus.post(ArenaEvent.class, arena, ArenaModificationType.CREATION)) {
      return;
    }
    this.arenas.put(name, arena);
  }

  public synchronized @Nullable Arena getArena(final String name) {
    return this.arenas.get(name);
  }

  public synchronized void removeArena(final String name) {
    final Arena arena = this.arenas.get(name);
    if (arena == null) {
      return;
    }
    final ApiEventBus bus = EventBusProvider.getBus();
    if (bus.post(ArenaEvent.class, arena, ArenaModificationType.DELETION)) {
      return;
    }
    final Path data = IOUtils.getPluginDataFolderPath();
    final Path parent = data.resolve("schematics/arenas");
    final Path schematic = parent.resolve(name);
    IOUtils.deleteFileIfExisting(schematic);
    this.arenas.remove(name);
  }

  public synchronized Map<String, Arena> getArenas() {
    return this.arenas;
  }

  public synchronized Set<@KeyFor("this.arenas") String> getArenaNames() {
    return this.arenas.keySet();
  }

  @Override
  public Long getId() {
    return this.id;
  }
}
