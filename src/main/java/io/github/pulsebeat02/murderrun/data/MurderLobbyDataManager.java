package io.github.pulsebeat02.murderrun.data;

import io.github.pulsebeat02.murderrun.MurderRun;
import io.github.pulsebeat02.murderrun.game.lobby.MurderLobbyManager;

public final class MurderLobbyDataManager extends PluginDataManager<MurderLobbyManager> {

  public MurderLobbyDataManager(final MurderRun run) {
    super(run, MurderLobbyManager.class, "lobbies.json");
  }
}
