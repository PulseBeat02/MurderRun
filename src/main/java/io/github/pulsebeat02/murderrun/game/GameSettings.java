package io.github.pulsebeat02.murderrun.game;

import io.github.pulsebeat02.murderrun.arena.MurderArena;
import org.bukkit.Location;

public final class GameSettings {

  private MurderArena arena;
  private int murdererCount;
  private int carPartCount;
  private Location lobbySpawn;

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

  public Location getLobbySpawn() {
    return this.lobbySpawn;
  }

  public void setLobbySpawn(final Location lobbySpawn) {
    this.lobbySpawn = lobbySpawn;
  }

  public MurderArena getArena() {
    return this.arena;
  }

  public void setArena(final MurderArena arena) {
    this.arena = arena;
  }
}
