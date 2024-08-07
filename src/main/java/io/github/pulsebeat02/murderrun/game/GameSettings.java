package io.github.pulsebeat02.murderrun.game;

import io.github.pulsebeat02.murderrun.game.arena.Arena;
import io.github.pulsebeat02.murderrun.game.lobby.Lobby;
import org.checkerframework.checker.nullness.qual.Nullable;

public final class GameSettings {

  private @Nullable Arena arena;
  private @Nullable Lobby lobby;
  private int murdererCount;
  private int carPartCount;

  public GameSettings() {
    this.arena = null;
    this.lobby = null;
    this.murdererCount = 1;
    this.carPartCount = 3;
  }

  public int getMurdererCount() {
    return this.murdererCount;
  }

  public void setMurdererCount(final int murdererCount) {
    this.murdererCount = murdererCount;
  }

  public int getCarPartCount() {
    return this.carPartCount;
  }

  public void setCarPartCount(final int carPartCount) {
    this.carPartCount = carPartCount;
  }

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
