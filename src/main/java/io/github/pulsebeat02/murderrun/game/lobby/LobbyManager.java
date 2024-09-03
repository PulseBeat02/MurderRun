package io.github.pulsebeat02.murderrun.game.lobby;

import java.util.HashMap;
import java.util.Map;
import java.util.Set;
import org.bukkit.Location;
import org.checkerframework.checker.nullness.qual.KeyFor;
import org.checkerframework.checker.nullness.qual.Nullable;

public final class LobbyManager {

  private final Map<String, Lobby> lobbies;

  public LobbyManager() {
    this.lobbies = new HashMap<>();
  }

  public void addLobby(final String name, final Location spawn) {
    final Lobby lobby = new Lobby(name, spawn);
    this.lobbies.put(name, lobby);
  }

  public void removeLobby(final String name) {
    this.lobbies.remove(name);
  }

  public @Nullable Lobby getLobby(final String name) {
    return this.lobbies.get(name);
  }

  public Map<String, Lobby> getLobbies() {
    return this.lobbies;
  }

  public Set<@KeyFor("this.lobbies") String> getLobbyNames() {
    return this.lobbies.keySet();
  }
}
