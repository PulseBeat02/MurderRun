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

  public Lobby getLobby() {
    if (this.lobby == null) {
      throw new AssertionError("Lobby wasn't created in game settings!");
    }
    return this.lobby;
  }

  public void setLobby(final @Nullable Lobby lobbySpawn) {
    this.lobby = lobbySpawn;
  }

  public Arena getArena() {
    if (this.arena == null) {
      throw new AssertionError("Arena wasn't created in game settings!");
    }
    return this.arena;
  }

  public void setArena(final @Nullable Arena arena) {
    this.arena = arena;
  }
}
