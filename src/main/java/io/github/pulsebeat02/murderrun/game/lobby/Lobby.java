package io.github.pulsebeat02.murderrun.game.lobby;

import io.github.pulsebeat02.murderrun.hibernate.converters.LocationConverter;
import jakarta.persistence.Column;
import jakarta.persistence.Convert;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import org.bukkit.Location;

@Entity
@Table(name = "lobby")
public final class Lobby {

  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  private Long id;

  @Column(name = "name")
  private final String name;

  @Convert(converter = LocationConverter.class)
  @Column(name = "lobby_spawn")
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
