package io.github.pulsebeat02.murderrun.config;

import org.bukkit.Location;

public final class GameConfiguration {

  private int murdererCount;
  private int carPartCount;
  private Location firstCorner;
  private Location secondCorner;
  private Location truckLocation;
  private Location lobbySpawn;
  private Location mapSpawn;

  public GameConfiguration() {}

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

  public Location getFirstCorner() {
    return this.firstCorner;
  }

  public void setFirstCorner(final Location firstCorner) {
    this.firstCorner = firstCorner;
  }

  public Location getSecondCorner() {
    return this.secondCorner;
  }

  public void setSecondCorner(final Location secondCorner) {
    this.secondCorner = secondCorner;
  }

  public Location getTruckLocation() {
    return this.truckLocation;
  }

  public void setTruckLocation(final Location truckLocation) {
    this.truckLocation = truckLocation;
  }

  public Location getLobbySpawn() {
    return this.lobbySpawn;
  }

  public void setLobbySpawn(final Location lobbySpawn) {
    this.lobbySpawn = lobbySpawn;
  }

  public Location getMapSpawn() {
    return this.mapSpawn;
  }

  public void setMapSpawn(final Location mapSpawn) {
    this.mapSpawn = mapSpawn;
  }
}
