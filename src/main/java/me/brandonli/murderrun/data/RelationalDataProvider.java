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
package me.brandonli.murderrun.data;

import me.brandonli.murderrun.MurderRun;
import me.brandonli.murderrun.data.hibernate.HibernateManager;
import me.brandonli.murderrun.data.json.ArenaCreationDataJSONMapper;
import me.brandonli.murderrun.data.json.ArenaDataJSONMapper;
import me.brandonli.murderrun.data.json.LobbyDataJSONMapper;
import me.brandonli.murderrun.data.json.StatisticJSONMapper;
import me.brandonli.murderrun.data.yaml.ConfigurationManager;
import me.brandonli.murderrun.data.yaml.PluginDataConfigurationMapper;
import me.brandonli.murderrun.game.arena.ArenaManager;
import me.brandonli.murderrun.game.lobby.LobbyManager;
import me.brandonli.murderrun.game.statistics.StatisticsManager;
import me.brandonli.murderrun.gui.arena.ArenaCreationManager;

public final class RelationalDataProvider {

  private final MurderRun plugin;
  private HibernateManager hibernate;
  private final ConfigurationManager<ArenaManager> arenas;
  private final ConfigurationManager<LobbyManager> lobbies;
  private final ConfigurationManager<StatisticsManager> statistics;
  private final ConfigurationManager<ArenaCreationManager> arenaCreation;

  public RelationalDataProvider(final MurderRun plugin) {
    final PluginDataConfigurationMapper mapper = plugin.getConfiguration();
    final RelationalDataMethod method = mapper.getRelationalDataMethod();
    this.plugin = plugin;
    switch (method) {
      case JSON:
        this.arenas = new ArenaDataJSONMapper();
        this.lobbies = new LobbyDataJSONMapper();
        this.statistics = new StatisticJSONMapper();
        this.arenaCreation = new ArenaCreationDataJSONMapper();
        break;
      case SQL:
        this.hibernate = new HibernateManager(this.plugin);
        this.arenas = this.hibernate.getArenaController();
        this.lobbies = this.hibernate.getLobbyController();
        this.statistics = this.hibernate.getStatisticsController();
        this.arenaCreation = this.hibernate.getArenaCreationController();
        break;
      default:
        throw new UnsupportedOperationException("Unsupported provider method!");
    }
  }

  public void shutdown() {
    if (this.hibernate != null) {
      this.hibernate.shutdown();
    }
    this.arenas.shutdown();
    this.lobbies.shutdown();
    this.statistics.shutdown();
  }

  public MurderRun getPlugin() {
    return this.plugin;
  }

  public HibernateManager getHibernate() {
    return this.hibernate;
  }

  public ConfigurationManager<ArenaManager> getArenas() {
    return this.arenas;
  }

  public ConfigurationManager<LobbyManager> getLobbies() {
    return this.lobbies;
  }

  public ConfigurationManager<StatisticsManager> getStatistics() {
    return this.statistics;
  }

  public ConfigurationManager<ArenaCreationManager> getArenaCreation() {
    return this.arenaCreation;
  }
}
