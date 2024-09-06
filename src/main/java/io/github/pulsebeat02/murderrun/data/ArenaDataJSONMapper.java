package io.github.pulsebeat02.murderrun.data;

import io.github.pulsebeat02.murderrun.game.arena.ArenaManager;

public final class ArenaDataJSONMapper extends AbstractJSONDataManager<ArenaManager> {

  public ArenaDataJSONMapper() {
    super(ArenaManager.class, "arenas.json");
  }
}
