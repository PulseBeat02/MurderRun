package io.github.pulsebeat02.murderrun.game.statistics;

import io.github.pulsebeat02.murderrun.game.player.GamePlayer;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;
import org.bukkit.OfflinePlayer;
import org.bukkit.entity.Player;

public final class StatisticsManager {

  private final Map<UUID, PlayerStatistics> map;

  public StatisticsManager() {
    this.map = new HashMap<>();
  }

  public PlayerStatistics getOrCreatePlayerStatistic(final GamePlayer player) {
    final Player internal = player.getInternalPlayer();
    return this.getOrCreatePlayerStatistic(internal);
  }

  public PlayerStatistics getOrCreatePlayerStatistic(final OfflinePlayer player) {
    final UUID uuid = player.getUniqueId();
    return this.map.computeIfAbsent(uuid, k -> new PlayerStatistics(uuid));
  }
}
