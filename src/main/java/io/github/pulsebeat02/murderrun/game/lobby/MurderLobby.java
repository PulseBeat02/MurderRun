package io.github.pulsebeat02.murderrun.game.lobby;

import org.bukkit.Location;

public final class MurderLobby {

  private final Location lobbySpawn;

  public MurderLobby(final Location lobbySpawn) {
    this.lobbySpawn = lobbySpawn;
  }

  public Location getLobbySpawn() {
    return this.lobbySpawn;
  }
}
