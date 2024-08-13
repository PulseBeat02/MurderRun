package io.github.pulsebeat02.murderrun.data;

import io.github.pulsebeat02.murderrun.MurderRun;
import io.github.pulsebeat02.murderrun.game.arena.ArenaManager;

public final class ArenaDataConfigurationMapper extends DataConfigurationManager<ArenaManager> {

  public ArenaDataConfigurationMapper(final MurderRun run) {
    super(run, ArenaManager.class, "arenas.json");
  }
}
