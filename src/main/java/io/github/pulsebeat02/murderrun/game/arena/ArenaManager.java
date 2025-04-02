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

import io.github.pulsebeat02.murderrun.data.hibernate.identifier.HibernateSerializable;
import io.github.pulsebeat02.murderrun.game.map.Schematic;
import io.github.pulsebeat02.murderrun.utils.IOUtils;
import jakarta.persistence.*;
import java.io.Serial;
import java.io.Serializable;
import java.nio.file.Path;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;
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

  public void addArena(
    final String name,
    final Location[] corners,
    final Location[] itemLocations,
    final Location spawn,
    final Location truck
  ) {
    final Schematic schematic = Schematic.copyAndCreateSchematic(name, corners, true);
    final Arena arena = new Arena(schematic, name, corners, itemLocations, spawn, truck);
    this.arenas.put(name, arena);
  }

  public @Nullable Arena getArena(final String name) {
    return this.arenas.get(name);
  }

  public void removeArena(final String name) {
    final Path data = IOUtils.getPluginDataFolderPath();
    final Path parent = data.resolve("schematics/arenas");
    final Path schematic = parent.resolve(name);
    IOUtils.deleteFileIfExisting(schematic);
    this.arenas.remove(name);
  }

  public Map<String, Arena> getArenas() {
    return this.arenas;
  }

  public Set<@KeyFor("this.arenas") String> getArenaNames() {
    return this.arenas.keySet();
  }

  @Override
  public Long getId() {
    return this.id;
  }
}
