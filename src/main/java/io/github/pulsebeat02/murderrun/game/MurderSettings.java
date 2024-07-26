package io.github.pulsebeat02.murderrun.game;

import io.github.pulsebeat02.murderrun.arena.MurderArena;
import io.github.pulsebeat02.murderrun.lobby.MurderLobby;
import org.checkerframework.checker.nullness.qual.Nullable;

public final class MurderSettings {

  private @Nullable MurderArena arena;
  private @Nullable MurderLobby lobby;
  private int murdererCount;
  private int carPartCount;

  public MurderSettings() {
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

  public MurderLobby getLobby() {
    if (this.lobby == null) {
      throw new AssertionError("Lobby wasn't created in game settings!");
    }
    return this.lobby;
  }

  public void setLobby(final @Nullable MurderLobby lobbySpawn) {
    this.lobby = lobbySpawn;
  }

  public MurderArena getArena() {
    if (this.arena == null) {
      throw new AssertionError("Arena wasn't created in game settings!");
    }
    return this.arena;
  }

  public void setArena(final @Nullable MurderArena arena) {
    this.arena = arena;
  }
}
