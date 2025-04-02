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
package io.github.pulsebeat02.murderrun.game.statistics;

import io.github.pulsebeat02.murderrun.api.event.ApiEventBus;
import io.github.pulsebeat02.murderrun.api.event.EventBusProvider;
import io.github.pulsebeat02.murderrun.api.event.contract.statistic.StatisticsEvent;
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
  @Column(name = "id", updatable = false, nullable = false)
  private Long id;

  @Column(name = "uuid")
  @Convert(converter = UUIDConverter.class)
  private UUID uuid;

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

  private transient ApiEventBus bus;

  public PlayerStatistics(final UUID uuid) {
    this.uuid = uuid;
    this.fastestWinKiller = -1;
    this.fastestWinSurvivor = -1;
    this.bus = EventBusProvider.getBus();
  }

  public PlayerStatistics() {}

  public void insertFastestWinKiller(final long win) {
    if (this.bus.post(StatisticsEvent.class, StatisticsEvent.StatisticsType.FASTEST_KILLER_WIN, win)) {
      return;
    }
    this.fastestWinKiller = Math.min(this.fastestWinKiller, win);
  }

  public void insertFastestWinSurvivor(final long win) {
    if (this.bus.post(StatisticsEvent.class, StatisticsEvent.StatisticsType.FASTEST_SURVIVOR_WIN, win)) {
      return;
    }
    this.fastestWinSurvivor = Math.min(this.fastestWinSurvivor, win);
  }

  public void incrementTotalKills() {
    if (this.bus.post(StatisticsEvent.class, StatisticsEvent.StatisticsType.TOTAL_KILLS, 1)) {
      return;
    }
    this.totalKills++;
  }

  public void incrementTotalDeaths() {
    if (this.bus.post(StatisticsEvent.class, StatisticsEvent.StatisticsType.TOTAL_DEATHS, 1)) {
      return;
    }
    this.totalDeaths++;
  }

  public void incrementTotalWins() {
    if (this.bus.post(StatisticsEvent.class, StatisticsEvent.StatisticsType.TOTAL_WINS, 1)) {
      return;
    }
    this.totalWins++;
    this.calculateWinLossRatio();
    this.incrementTotalGames();
  }

  public void incrementTotalLosses() {
    if (this.bus.post(StatisticsEvent.class, StatisticsEvent.StatisticsType.TOTAL_LOSSES, 1)) {
      return;
    }
    this.totalLosses++;
    this.calculateWinLossRatio();
    this.incrementTotalGames();
  }

  public void incrementTotalGames() {
    if (this.bus.post(StatisticsEvent.class, StatisticsEvent.StatisticsType.TOTAL_GAMES, 1)) {
      return;
    }
    this.totalGames++;
  }

  public void calculateWinLossRatio() {
    if (this.totalLosses != 0) {
      final float recalculatedWinLossRatio = (float) this.totalWins / this.totalLosses;
      if (this.bus.post(StatisticsEvent.class, StatisticsEvent.StatisticsType.WIN_LOSS_RATIO, recalculatedWinLossRatio)) {
        return;
      }
      this.winLossRatio = recalculatedWinLossRatio;
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
