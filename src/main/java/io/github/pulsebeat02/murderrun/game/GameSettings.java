package io.github.pulsebeat02.murderrun.game;

import io.github.pulsebeat02.murderrun.game.arena.Arena;
import io.github.pulsebeat02.murderrun.game.lobby.Lobby;
import org.checkerframework.checker.nullness.qual.Nullable;

public final class GameSettings {

  private @Nullable Arena arena;
  private @Nullable Lobby lobby;

  public @Nullable Lobby getLobby() {
    return this.lobby;
  }

  public void setLobby(final @Nullable Lobby lobbySpawn) {
    this.lobby = lobbySpawn;
  }

  public @Nullable Arena getArena() {
    return this.arena;
  }

  public void setArena(final @Nullable Arena arena) {
    this.arena = arena;
  }
}
