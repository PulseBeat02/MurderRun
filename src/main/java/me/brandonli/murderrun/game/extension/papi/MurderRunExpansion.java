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

import me.brandonli.murderrun.MurderRun;
import me.brandonli.murderrun.game.statistics.PlayerStatistics;
import me.brandonli.murderrun.game.statistics.StatisticsManager;
import me.clip.placeholderapi.expansion.PlaceholderExpansion;
import org.bukkit.OfflinePlayer;
import org.checkerframework.checker.nullness.qual.NonNull;
import org.checkerframework.checker.nullness.qual.Nullable;

public final class MurderRunExpansion extends PlaceholderExpansion {

  private static final String AUTHORS = "PulseBeat_02";
  private static final String VERSION = "v1.0.0";
  private static final String MURDER_RUN_IDENTIFIER = "murderrun";

  private final PAPIPlaceholderParser handler;
  private final MurderRun plugin;

  public MurderRunExpansion(final MurderRun plugin) {
    this.handler = new PAPIPlaceholderParser();
    this.plugin = plugin;
  }

  @Override
  public @NonNull String getIdentifier() {
    return MURDER_RUN_IDENTIFIER;
  }

  @Override
  public @NonNull String getAuthor() {
    return AUTHORS;
  }

  @Override
  public @NonNull String getVersion() {
    return VERSION;
  }

  @Override
  public boolean persist() {
    return true;
  }

  @Override
  public @Nullable String onRequest(final OfflinePlayer player, final @NonNull String params) {
    final StatisticsManager manager = this.plugin.getStatisticsManager();
    final PlayerStatistics statistics = manager.getOrCreatePlayerStatistic(player);
    return this.handler.getPlaceholder(statistics, params);
  }
}
