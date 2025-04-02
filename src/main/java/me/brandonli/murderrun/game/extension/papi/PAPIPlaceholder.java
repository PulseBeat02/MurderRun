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

import java.util.function.Function;
import me.brandonli.murderrun.game.statistics.PlayerStatistics;

public final class PAPIPlaceholder {

  private final String name;
  private final Function<PlayerStatistics, Object> function;

  public PAPIPlaceholder(final String name, final Function<PlayerStatistics, Object> function) {
    this.name = name;
    this.function = function;
  }

  public String getStatistic(final PlayerStatistics statistics) {
    return String.valueOf(this.function.apply(statistics));
  }

  public String getName() {
    return this.name;
  }
}
