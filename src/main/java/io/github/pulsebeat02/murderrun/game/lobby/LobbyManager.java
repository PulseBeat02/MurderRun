package io.github.pulsebeat02.murderrun.game.lobby;

import jakarta.persistence.CascadeType;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.MapKeyColumn;
import jakarta.persistence.OneToMany;
import jakarta.persistence.Table;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;
import org.bukkit.Location;
import org.checkerframework.checker.nullness.qual.KeyFor;
import org.checkerframework.checker.nullness.qual.Nullable;

@Entity
@Table(name = "lobby_manager")
public final class LobbyManager {

  @Id
  private transient String id = "lobby_manager";

  @OneToMany(cascade = CascadeType.ALL, orphanRemoval = true)
  @MapKeyColumn(name = "name")
  @JoinColumn(name = "lobby_manager_id")
  @Column(name = "lobbies")
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
