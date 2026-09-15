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
package me.brandonli.murderrun.game.extension.papi;

import java.util.Collection;
import java.util.concurrent.ConcurrentHashMap;
import java.util.function.Function;
import me.brandonli.murderrun.game.statistics.PlayerStatistics;
import org.checkerframework.checker.nullness.qual.Nullable;

public final class PAPIPlaceholderParser {

  private static final Collection<PAPIPlaceholder> PLACEHOLDERS = ConcurrentHashMap.newKeySet();

  static {
    of("fastest_win_killer", checkValue(PlayerStatistics::getFastestWinKiller));
    of("fastest_win_survivor", checkValue(PlayerStatistics::getFastestWinSurvivor));
    of("total_kills", PlayerStatistics::getTotalKills);
    of("total_deaths", PlayerStatistics::getTotalDeaths);
    of("total_wins", PlayerStatistics::getTotalWins);
    of("total_losses", PlayerStatistics::getTotalLosses);
    of("total_games", PlayerStatistics::getTotalGames);
    of("win_loss_ratio", PlayerStatistics::getWinLossRatio);
  }

  private static Function<PlayerStatistics, Object> checkValue(
      final Function<PlayerStatistics, Long> function) {
    return statistics -> {
      final long value = function.apply(statistics);
      if (value == Long.MAX_VALUE) {
        return "N/A";
      }
      return value;
    };
  }

  private static PAPIPlaceholder of(
      final String name, final Function<PlayerStatistics, Object> function) {
    final PAPIPlaceholder holder = new PAPIPlaceholder(name, function);
    PLACEHOLDERS.add(holder);
    return holder;
  }

  public @Nullable String getPlaceholder(final PlayerStatistics statistics, final String target) {
    for (final PAPIPlaceholder placeholder : PLACEHOLDERS) {
      final String name = placeholder.getName();
      if (name.equalsIgnoreCase(target)) {
        return placeholder.getStatistic(statistics);
      }
    }
    return null;
  }
}
