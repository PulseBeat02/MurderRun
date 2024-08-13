package io.github.pulsebeat02.murderrun.data;

import io.github.pulsebeat02.murderrun.MurderRun;
import io.github.pulsebeat02.murderrun.game.lobby.LobbyManager;

public final class LobbyDataJSONMapper extends AbstractJSONDataManager<LobbyManager> {

  public LobbyDataJSONMapper(final MurderRun run) {
    super(LobbyManager.class, "lobbies.json");
  }
}
