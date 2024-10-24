package io.github.pulsebeat02.murderrun.game.statistics;

import io.github.pulsebeat02.murderrun.data.hibernate.identifier.HibernateSerializable;
import io.github.pulsebeat02.murderrun.data.hibernate.converters.UUIDConverter;
import io.github.pulsebeat02.murderrun.game.player.GamePlayer;
import jakarta.persistence.*;
import java.io.Serial;
import java.io.Serializable;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;
import org.bukkit.OfflinePlayer;
import org.bukkit.entity.Player;

@Entity
@Table(name = "statistics_manager")
public final class StatisticsManager implements Serializable, HibernateSerializable {

  @Serial
  private static final long serialVersionUID = 1848424616462443310L;

  @Id
  @GeneratedValue
  @Column(name = "id")
  private Long id;

  @OneToMany(orphanRemoval = true)
  @MapKeyColumn(name = "uuid")
  @JoinColumn(name = "statistics_manager_id")
  @Column(name = "player_statistics")
  @Convert(converter = UUIDConverter.class, attributeName = "key.uuid")
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

  @Override
  public long getId() {
    return this.id;
  }
}
