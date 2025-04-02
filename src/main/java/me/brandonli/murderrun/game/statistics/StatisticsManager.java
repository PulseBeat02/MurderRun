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
package me.brandonli.murderrun.game.statistics;

import jakarta.persistence.*;
import java.io.Serial;
import java.io.Serializable;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;
import me.brandonli.murderrun.data.hibernate.converters.UUIDConverter;
import me.brandonli.murderrun.data.hibernate.identifier.HibernateSerializable;
import me.brandonli.murderrun.game.player.GamePlayer;
import org.bukkit.OfflinePlayer;

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
    return player.applyFunction(this::getOrCreatePlayerStatistic);
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
