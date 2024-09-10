package io.github.pulsebeat02.murderrun.data.json;

import io.github.pulsebeat02.murderrun.game.lobby.LobbyManager;

public final class LobbyDataJSONMapper extends AbstractJSONDataManager<LobbyManager> {

  public LobbyDataJSONMapper() {
    super("lobbies.json");
  }
}
