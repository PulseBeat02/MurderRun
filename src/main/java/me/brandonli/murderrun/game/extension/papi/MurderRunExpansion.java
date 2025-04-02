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
package me.brandonli.murderrun.game.extension.papi;

import java.util.List;
import me.brandonli.murderrun.MurderRun;
import me.brandonli.murderrun.game.statistics.PlayerStatistics;
import me.brandonli.murderrun.game.statistics.StatisticsManager;
import me.clip.placeholderapi.expansion.PlaceholderExpansion;
import org.bukkit.OfflinePlayer;
import org.bukkit.plugin.PluginDescriptionFile;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public final class MurderRunExpansion extends PlaceholderExpansion {

  private static final String MURDER_RUN_IDENTIFIER = "murderrun";

  private final PAPIPlaceholderParser handler;
  private final MurderRun plugin;
  private final String authors;
  private final String version;

  public MurderRunExpansion(final MurderRun plugin) {
    final PluginDescriptionFile description = plugin.getDescription();
    final List<String> authors = description.getAuthors();
    this.handler = new PAPIPlaceholderParser();
    this.plugin = plugin;
    this.authors = String.join(", ", authors);
    this.version = description.getVersion();
  }

  @Override
  public @NotNull String getIdentifier() {
    return MURDER_RUN_IDENTIFIER;
  }

  @Override
  public @NotNull String getAuthor() {
    return this.authors;
  }

  @Override
  public @NotNull String getVersion() {
    return this.version;
  }

  @Override
  public boolean persist() {
    return true;
  }

  @Override
  public @Nullable String onRequest(final OfflinePlayer player, final @NotNull String params) {
    final StatisticsManager manager = this.plugin.getStatisticsManager();
    final PlayerStatistics statistics = manager.getOrCreatePlayerStatistic(player);
    return this.handler.getPlaceholder(statistics, params);
  }
}
