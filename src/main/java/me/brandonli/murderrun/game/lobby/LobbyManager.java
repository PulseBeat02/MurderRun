/*

MIT License

Copyright (c) 2024 Brandon Li

Permission is hereby granted, free of charge, to any person obtaining a copy
of this software and associated documentation files (the "Software"), to deal
in the Software without restriction, including without limitation the rights
to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
copies of the Software, and to permit persons to whom the Software is
furnished to do so, subject to the following conditions:

The above copyright notice and this permission notice shall be included in all
copies or substantial portions of the Software.

THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE
SOFTWARE.

*/
package me.brandonli.murderrun.game.lobby;

import jakarta.persistence.*;
import java.io.Serial;
import java.io.Serializable;
import java.nio.file.Path;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;
import me.brandonli.murderrun.api.event.ApiEventBus;
import me.brandonli.murderrun.api.event.EventBusProvider;
import me.brandonli.murderrun.api.event.contract.lobby.LobbyEvent;
import me.brandonli.murderrun.api.event.contract.lobby.LobbyModificationType;
import me.brandonli.murderrun.data.hibernate.identifier.HibernateSerializable;
import me.brandonli.murderrun.game.map.Schematic;
import me.brandonli.murderrun.utils.IOUtils;
import org.bukkit.Location;
import org.checkerframework.checker.nullness.qual.KeyFor;
import org.checkerframework.checker.nullness.qual.Nullable;

@Entity
@Table(name = "lobby_manager")
public final class LobbyManager implements Serializable, HibernateSerializable {

  @Serial
  private static final long serialVersionUID = 7490295092814979132L;

  @Id
  @GeneratedValue
  @Column(name = "id", updatable = false, nullable = false)
  private Long id;

  @OneToMany(orphanRemoval = true, cascade = CascadeType.ALL)
  @MapKeyColumn(name = "name")
  @JoinColumn(name = "lobby_manager_id")
  @Column(name = "lobby")
  private final Map<String, Lobby> lobbies;

  public LobbyManager() {
    this.lobbies = new HashMap<>();
  }

  public void addInternalLobby(final Lobby lobby) {
    final ApiEventBus bus = EventBusProvider.getBus();
    if (bus.post(LobbyEvent.class, lobby, LobbyModificationType.CREATION)) {
      return;
    }
    final String name = lobby.getName();
    this.lobbies.put(name, lobby);
  }

  public void addLobby(final String name, final Location[] corners, final Location spawn) {
    final ApiEventBus bus = EventBusProvider.getBus();
    final Schematic schematic = Schematic.copyAndCreateSchematic(name, corners, false);
    final Lobby lobby = new Lobby(schematic, name, corners, spawn);
    if (bus.post(LobbyEvent.class, lobby, LobbyModificationType.CREATION)) {
      return;
    }
    this.lobbies.put(name, lobby);
  }

  public void removeLobby(final String name) {
    final Lobby lobby = this.lobbies.get(name);
    if (lobby == null) {
      return;
    }
    final ApiEventBus bus = EventBusProvider.getBus();
    if (bus.post(LobbyEvent.class, lobby, LobbyModificationType.DELETION)) {
      return;
    }
    final Path data = IOUtils.getPluginDataFolderPath();
    final Path parent = data.resolve("schematics/lobbies");
    final Path schematic = parent.resolve(name);
    IOUtils.deleteFileIfExisting(schematic);
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

  @Override
  public Long getId() {
    return this.id;
  }
}
