package io.github.pulsebeat02.murderrun.data;

import io.github.pulsebeat02.murderrun.MurderRun;
import io.github.pulsebeat02.murderrun.game.lobby.LobbyManager;

public final class LobbyDataConfigurationMapper extends DataConfigurationManager<LobbyManager> {

  public LobbyDataConfigurationMapper(final MurderRun run) {
    super(run, LobbyManager.class, "lobbies.json");
  }
}
