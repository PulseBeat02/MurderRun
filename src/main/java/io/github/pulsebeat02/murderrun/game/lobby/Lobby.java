package io.github.pulsebeat02.murderrun.game.lobby;

import io.github.pulsebeat02.murderrun.data.hibernate.HibernateIdentifiers;
import io.github.pulsebeat02.murderrun.data.hibernate.converters.LocationConverter;
import jakarta.persistence.*;
import java.io.Serial;
import java.io.Serializable;
import org.bukkit.Location;

@Entity
@Table(name = "lobby")
public final class Lobby implements Serializable {

  @Serial
  private static final long serialVersionUID = -3340383856074756744L;

  @Id
  @GeneratedValue
  @Column(name = "id")
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
