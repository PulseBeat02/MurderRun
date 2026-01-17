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
package me.brandonli.murderrun.api.event.contract.statistic;

public final class StatisticsType<T extends Number> {

  public static final StatisticsType<Long> FASTEST_KILLER_WIN = of("FASTEST_KILLER_WIN", Long.TYPE);
  public static final StatisticsType<Long> FASTEST_SURVIVOR_WIN =
      of("FASTEST_SURVIVOR_WIN", Long.TYPE);
  public static final StatisticsType<Integer> TOTAL_KILLS = of("TOTAL_KILLS", Integer.TYPE);
  public static final StatisticsType<Integer> TOTAL_DEATHS = of("TOTAL_DEATHS", Integer.TYPE);
  public static final StatisticsType<Integer> TOTAL_WINS = of("TOTAL_WINS", Integer.TYPE);
  public static final StatisticsType<Integer> TOTAL_LOSSES = of("TOTAL_LOSSES", Integer.TYPE);
  public static final StatisticsType<Integer> TOTAL_GAMES = of("TOTAL_GAMES", Integer.TYPE);
  public static final StatisticsType<Float> WIN_LOSS_RATIO = of("WIN_LOSS_RATIO", Float.TYPE);

  private static <T extends Number> StatisticsType<T> of(final String name, final Class<T> clazz) {
    return new StatisticsType<>(name, clazz);
  }

  private final String name;
  private final Class<T> clazz;

  private StatisticsType(final String name, final Class<T> clazz) {
    this.name = name;
    this.clazz = clazz;
  }

  public Class<T> getType() {
    return this.clazz;
  }

  @Override
  public String toString() {
    return this.name;
  }
}
