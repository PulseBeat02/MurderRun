package io.github.pulsebeat02.murderrun.game.statistics;

import io.github.pulsebeat02.murderrun.data.hibernate.HibernateIdentifiers;
import io.github.pulsebeat02.murderrun.data.hibernate.converters.UUIDConverter;
import jakarta.persistence.*;
import java.io.Serial;
import java.io.Serializable;
import java.util.UUID;

@Entity
@Table(name = "player_statistics")
public final class PlayerStatistics implements Serializable {

  @Serial
  private static final long serialVersionUID = 8818715610268669533L;

  @Id
  @GeneratedValue
  @Column(name = "id")
  private Long id;

  @Column(name = "uuid")
  @Convert(converter = UUIDConverter.class)
  private final UUID uuid;

  @Column(name = "fastest_win_killer")
  private long fastestWinKiller;

  @Column(name = "fastest_win_survivor")
  private long fastestWinSurvivor;

  @Column(name = "total_kills")
  private int totalKills;

  @Column(name = "total_deaths")
  private int totalDeaths;

  @Column(name = "total_wins")
  private int totalWins;

  @Column(name = "total_losses")
  private int totalLosses;

  @Column(name = "total_games")
  private int totalGames;

  @Column(name = "win_loss_ratio")
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
