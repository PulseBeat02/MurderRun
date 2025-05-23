/*
 * This file is part of Murder Run, a spin-off game-mode of Dead by Daylight
 * Copyright (C) Brandon Li <https://brandonli.me/>
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program.  If not, see <https://www.gnu.org/licenses/>.
 */
package me.brandonli.murderrun.game.statistics;

import jakarta.persistence.*;
import java.io.Serial;
import java.io.Serializable;
import java.util.UUID;
import me.brandonli.murderrun.api.event.ApiEventBus;
import me.brandonli.murderrun.api.event.EventBusProvider;
import me.brandonli.murderrun.api.event.contract.statistic.StatisticsEvent;
import me.brandonli.murderrun.api.event.contract.statistic.StatisticsType;
import me.brandonli.murderrun.data.hibernate.converters.UUIDConverter;

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

  public PlayerStatistics(final UUID uuid) {
    this.uuid = uuid;
    this.fastestWinKiller = -1;
    this.fastestWinSurvivor = -1;
  }

  public PlayerStatistics() {}

  public void insertFastestWinKiller(final long win) {
    final ApiEventBus bus = EventBusProvider.getBus();
    if (bus.post(StatisticsEvent.class, StatisticsType.FASTEST_KILLER_WIN, win)) {
      return;
    }
    this.fastestWinKiller = Math.min(this.fastestWinKiller, win);
  }

  public void insertFastestWinSurvivor(final long win) {
    final ApiEventBus bus = EventBusProvider.getBus();
    if (bus.post(StatisticsEvent.class, StatisticsType.FASTEST_SURVIVOR_WIN, win)) {
      return;
    }
    this.fastestWinSurvivor = Math.min(this.fastestWinSurvivor, win);
  }

  public void incrementTotalKills() {
    final ApiEventBus bus = EventBusProvider.getBus();
    if (bus.post(StatisticsEvent.class, StatisticsType.TOTAL_KILLS, 1)) {
      return;
    }
    this.totalKills++;
  }

  public void incrementTotalDeaths() {
    final ApiEventBus bus = EventBusProvider.getBus();
    if (bus.post(StatisticsEvent.class, StatisticsType.TOTAL_DEATHS, 1)) {
      return;
    }
    this.totalDeaths++;
  }

  public void incrementTotalWins() {
    final ApiEventBus bus = EventBusProvider.getBus();
    if (bus.post(StatisticsEvent.class, StatisticsType.TOTAL_WINS, 1)) {
      return;
    }
    this.totalWins++;
    this.calculateWinLossRatio();
    this.incrementTotalGames();
  }

  public void incrementTotalLosses() {
    final ApiEventBus bus = EventBusProvider.getBus();
    if (bus.post(StatisticsEvent.class, StatisticsType.TOTAL_LOSSES, 1)) {
      return;
    }
    this.totalLosses++;
    this.calculateWinLossRatio();
    this.incrementTotalGames();
  }

  public void incrementTotalGames() {
    final ApiEventBus bus = EventBusProvider.getBus();
    if (bus.post(StatisticsEvent.class, StatisticsType.TOTAL_GAMES, 1)) {
      return;
    }
    this.totalGames++;
  }

  public void calculateWinLossRatio() {
    final ApiEventBus bus = EventBusProvider.getBus();
    if (this.totalLosses != 0) {
      final float recalculatedWinLossRatio = (float) this.totalWins / this.totalLosses;
      if (bus.post(StatisticsEvent.class, StatisticsType.WIN_LOSS_RATIO, recalculatedWinLossRatio)) {
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
