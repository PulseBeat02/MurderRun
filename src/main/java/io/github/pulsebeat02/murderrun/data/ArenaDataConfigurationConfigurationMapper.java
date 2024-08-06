package io.github.pulsebeat02.murderrun.data;

import io.github.pulsebeat02.murderrun.MurderRun;
import io.github.pulsebeat02.murderrun.game.arena.ArenaManager;

public final class ArenaDataConfigurationConfigurationMapper
    extends DataConfigurationManager<ArenaManager> {

  public ArenaDataConfigurationConfigurationMapper(final MurderRun run) {
    super(run, ArenaManager.class, "arenas.json");
  }
}
