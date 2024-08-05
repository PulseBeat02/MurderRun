package io.github.pulsebeat02.murderrun.data;

import io.github.pulsebeat02.murderrun.MurderRun;
import io.github.pulsebeat02.murderrun.game.arena.MurderArenaManager;

public final class MurderArenaDataManager extends PluginDataManager<MurderArenaManager> {

  public MurderArenaDataManager(final MurderRun run) {
    super(run, MurderArenaManager.class, "arenas.json");
  }
}
