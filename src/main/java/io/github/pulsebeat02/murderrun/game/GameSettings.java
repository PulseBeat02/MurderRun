package io.github.pulsebeat02.murderrun.game;

import io.github.pulsebeat02.murderrun.arena.MurderArena;
import io.github.pulsebeat02.murderrun.lobby.GameLobby;

public final class GameSettings {

  private MurderArena arena;
  private GameLobby lobby;
  private int murdererCount;
  private int carPartCount;

  public GameSettings() {}

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

  public GameLobby getLobby() {
    return this.lobby;
  }

  public void setLobby(final GameLobby lobbySpawn) {
    this.lobby = lobbySpawn;
  }

  public MurderArena getArena() {
    return this.arena;
  }

  public void setArena(final MurderArena arena) {
    this.arena = arena;
  }
}
