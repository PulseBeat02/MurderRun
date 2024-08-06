package io.github.pulsebeat02.murderrun.game.lobby;

import org.bukkit.Location;

public final class Lobby {

  private final Location lobbySpawn;

  public Lobby(final Location lobbySpawn) {
    this.lobbySpawn = lobbySpawn;
  }

  public Location getLobbySpawn() {
    return this.lobbySpawn;
  }
}
