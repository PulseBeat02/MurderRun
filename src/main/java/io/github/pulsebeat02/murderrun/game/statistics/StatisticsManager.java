package io.github.pulsebeat02.murderrun.game.statistics;

import io.github.pulsebeat02.murderrun.game.player.GamePlayer;
import jakarta.persistence.CascadeType;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.MapKeyColumn;
import jakarta.persistence.OneToMany;
import jakarta.persistence.Table;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;
import org.bukkit.OfflinePlayer;
import org.bukkit.entity.Player;

@Entity
@Table(name = "statistics_manager")
public final class StatisticsManager {

  @Id
  private transient String id = "statistics_manager";

  @OneToMany(cascade = CascadeType.ALL, orphanRemoval = true)
  @MapKeyColumn(name = "uuid")
  @JoinColumn(name = "statistics_manager_id")
  @Column(name = "player_statistics")
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
