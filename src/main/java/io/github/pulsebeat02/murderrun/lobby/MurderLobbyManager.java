package io.github.pulsebeat02.murderrun.lobby;

import java.util.HashMap;
import java.util.Map;
import java.util.Set;
import javax.annotation.Nullable;
import org.bukkit.Location;
import org.checkerframework.checker.nullness.qual.KeyFor;

public final class MurderLobbyManager {

  private final Map<String, MurderLobby> lobbies;

  public MurderLobbyManager() {
    this.lobbies = new HashMap<>();
  }

  public void addLobby(final String name, final Location spawn) {
    final MurderLobby lobby = new MurderLobby(spawn);
    this.lobbies.put(name, lobby);
  }

  public void removeLobby(final String name) {
    this.lobbies.remove(name);
  }

  public @Nullable MurderLobby getLobby(final String name) {
    return this.lobbies.get(name);
  }

  public Map<String, MurderLobby> getLobbies() {
    return this.lobbies;
  }

  public Set<@KeyFor("this.lobbies") String> getLobbyNames() {
    return this.lobbies.keySet();
  }
}
