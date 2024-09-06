package io.github.pulsebeat02.murderrun.game.statistics;

import java.util.UUID;

public final class PlayerStatistics {

  private final UUID uuid;

  private long fastestWinKiller;
  private long fastestWinSurvivor;
  private int totalKills;
  private int totalDeaths;
  private int totalWins;
  private int totalLosses;
  private int totalGames;
  private float winLossRatio;

  public PlayerStatistics(final UUID uuid) {
    this.uuid = uuid;
    this.fastestWinKiller = -1;
    this.fastestWinSurvivor = -1;
  }

  public void insertFastestWinKiller(final long win) {
    this.fastestWinKiller = Math.min(this.fastestWinKiller, win);
  }

  public void insertFastestWinSurvivor(final long win) {
    this.fastestWinSurvivor = Math.min(this.fastestWinSurvivor, win);
  }

  public void incrementTotalKills() {
    this.totalKills++;
  }

  public void incrementTotalDeaths() {
    this.totalDeaths++;
  }

  public void incrementTotalWins() {
    this.totalWins++;
    this.calculateWinLossRatio();
    this.incrementTotalGames();
  }

  public void incrementTotalLosses() {
    this.totalLosses++;
    this.calculateWinLossRatio();
    this.incrementTotalGames();
  }

  public void incrementTotalGames() { // dont need to update
    this.totalGames++;
  }

  public void calculateWinLossRatio() { // dont need to update
    if (this.totalLosses != 0) {
      this.winLossRatio = (float) this.totalWins / this.totalLosses;
    }
  }

  public UUID getUUID() {
    return this.uuid;
  }

  public long getFastestWinKiller() {
    return this.fastestWinKiller;
  }

  public long getFastestWinSurvivor() {
    return this.fastestWinSurvivor;
  }

  public int getTotalKills() {
    return this.totalKills;
  }

  public int getTotalDeaths() {
    return this.totalDeaths;
  }

  public int getTotalWins() {
    return this.totalWins;
  }

  public int getTotalLosses() {
    return this.totalLosses;
  }

  public int getTotalGames() {
    return this.totalGames;
  }

  public float getWinLossRatio() {
    return this.winLossRatio;
  }
}
