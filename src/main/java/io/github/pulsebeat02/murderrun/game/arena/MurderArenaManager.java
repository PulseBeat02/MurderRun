package io.github.pulsebeat02.murderrun.game.arena;

import io.github.pulsebeat02.murderrun.utils.MapUtils;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;
import javax.annotation.Nullable;
import org.bukkit.Location;
import org.checkerframework.checker.nullness.qual.KeyFor;

public final class MurderArenaManager {

  private final Map<String, MurderArena> arenas;

  public MurderArenaManager() {
    this.arenas = new HashMap<>();
  }

  public void addArena(
      final String name, final Location[] corners, final Location spawn, final Location truck) {
    final MurderArenaSchematic schematic = MapUtils.copyAndCreateSchematic(name, corners);
    final MurderArena arena = new MurderArena(schematic, name, corners, spawn, truck);
    this.arenas.put(name, arena);
  }

  public @Nullable MurderArena getArena(final String name) {
    return this.arenas.get(name);
  }

  public void removeArena(final String name) {
    this.arenas.remove(name);
  }

  public Map<String, MurderArena> getArenas() {
    return this.arenas;
  }

  public Set<@KeyFor("this.arenas") String> getArenaNames() {
    return this.arenas.keySet();
  }
}
