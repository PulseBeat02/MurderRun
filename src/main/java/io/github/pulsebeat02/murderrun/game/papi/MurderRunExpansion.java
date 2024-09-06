package io.github.pulsebeat02.murderrun.game.papi;

import io.github.pulsebeat02.murderrun.MurderRun;
import io.github.pulsebeat02.murderrun.game.statistics.PlayerStatistics;
import io.github.pulsebeat02.murderrun.game.statistics.StatisticsManager;
import java.util.List;
import me.clip.placeholderapi.expansion.PlaceholderExpansion;
import org.bukkit.OfflinePlayer;
import org.bukkit.plugin.PluginDescriptionFile;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public final class MurderRunExpansion extends PlaceholderExpansion {

  private static final String MURDER_RUN_IDENTIFIER = "murderrun";

  private final PlaceholderHandler handler;
  private final MurderRun plugin;
  private final String authors;
  private final String version;

  public MurderRunExpansion(final MurderRun plugin) {
    final PluginDescriptionFile description = plugin.getDescription();
    final List<String> authors = description.getAuthors();
    this.handler = new PlaceholderHandler();
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
