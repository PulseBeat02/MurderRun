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
package io.github.pulsebeat02.murderrun.game.statistics;

import io.github.pulsebeat02.murderrun.data.hibernate.converters.UUIDConverter;
import io.github.pulsebeat02.murderrun.data.hibernate.identifier.HibernateSerializable;
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
  @Column(name = "id", updatable = false, nullable = false)
  private Long id;

  @OneToMany(orphanRemoval = true, cascade = CascadeType.ALL)
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
  public Long getId() {
    return this.id;
  }
}
