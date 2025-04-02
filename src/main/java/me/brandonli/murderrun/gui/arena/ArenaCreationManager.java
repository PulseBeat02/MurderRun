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
import java.util.*;
import me.brandonli.murderrun.data.hibernate.identifier.HibernateSerializable;
import org.bukkit.Location;
import org.checkerframework.checker.nullness.qual.KeyFor;
import org.checkerframework.checker.nullness.qual.Nullable;

@Entity
@Table(name = "arena_creation_manager")
public final class ArenaCreationManager implements Serializable, HibernateSerializable {

  @Serial
  private static final long serialVersionUID = 2568177708134148153L;

  @Id
  @GeneratedValue
  @Column(name = "id", updatable = false, nullable = false)
  private Long id;

  @OneToMany(orphanRemoval = true, cascade = CascadeType.ALL)
  @MapKeyColumn(name = "name")
  @JoinColumn(name = "arena_creation_manager_id")
  @Column(name = "arena")
  private final Map<UUID, ArenaCreation> arenas;

  public ArenaCreationManager() {
    this.arenas = new HashMap<>();
  }

  public void addArena(
    final UUID uuid,
    final String arenaName,
    final Location spawn,
    final Location truck,
    final Location first,
    final Location second,
    final Collection<Location> itemLocations
  ) {
    final ArenaCreation creation = new ArenaCreation(arenaName, spawn, truck, first, second, itemLocations);
    this.arenas.put(uuid, creation);
  }

  public @Nullable ArenaCreation getArena(final UUID name) {
    return this.arenas.get(name);
  }

  public void removeArena(final UUID name) {
    this.arenas.remove(name);
  }

  public Map<UUID, ArenaCreation> getArenas() {
    return this.arenas;
  }

  public Set<@KeyFor("this.arenas") UUID> getPlayerData() {
    return this.arenas.keySet();
  }

  @Override
  public Long getId() {
    return this.id;
  }
}
