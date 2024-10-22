package io.github.pulsebeat02.murderrun.game.arena;

import jakarta.persistence.*;

import java.util.HashMap;
import java.util.Map;
import java.util.Set;
import org.bukkit.Location;
import org.checkerframework.checker.nullness.qual.KeyFor;
import org.checkerframework.checker.nullness.qual.Nullable;

@Entity
@Table(name = "arena_manager")
public final class ArenaManager {

  @Id
  @GeneratedValue
  @Column(name = "id")
  private Long id;

  @OneToMany(cascade = CascadeType.ALL, orphanRemoval = true)
  @MapKeyColumn(name = "name")
  @JoinColumn(name = "arena_manager_id")
  @Column(name = "arenas")
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
    final ArenaSchematic schematic = ArenaSchematic.copyAndCreateSchematic(name, corners);
    final Arena arena = new Arena(schematic, name, corners, itemLocations, spawn, truck);
    this.arenas.put(name, arena);
  }

  public @Nullable Arena getArena(final String name) {
    return this.arenas.get(name);
  }

  public void removeArena(final String name) {
    this.arenas.remove(name);
  }

  public Map<String, Arena> getArenas() {
    return this.arenas;
  }

  public Set<@KeyFor("this.arenas") String> getArenaNames() {
    return this.arenas.keySet();
  }
}
