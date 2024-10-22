package io.github.pulsebeat02.murderrun.game.lobby;

import io.github.pulsebeat02.murderrun.data.hibernate.HibernateIdentifiers;
import jakarta.persistence.*;
import java.io.Serial;
import java.io.Serializable;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;
import org.bukkit.Location;
import org.checkerframework.checker.nullness.qual.KeyFor;
import org.checkerframework.checker.nullness.qual.Nullable;

@Entity
@Table(name = "lobby_manager")
public final class LobbyManager implements Serializable {

  @Serial
  private static final long serialVersionUID = 7490295092814979132L;

  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  @Column(name = "id")
  private Long id = HibernateIdentifiers.LOBBY_MANAGER_ID;

  @OneToMany(cascade = CascadeType.ALL, orphanRemoval = true)
  @MapKeyColumn(name = "name")
  @JoinColumn(name = "lobby_manager_id")
  @Column(name = "lobby")
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
