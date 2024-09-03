package io.github.pulsebeat02.murderrun.game.lobby;

import org.bukkit.Location;

public final class Lobby {

  private final String name;
  private final Location lobbySpawn;

  public Lobby(final String name, final Location lobbySpawn) {
    this.name = name;
    this.lobbySpawn = lobbySpawn;
  }

  public String getName() {
    return this.name;
  }

  public Location getLobbySpawn() {
    return this.lobbySpawn;
  }
}
