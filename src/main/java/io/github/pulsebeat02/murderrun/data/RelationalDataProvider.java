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
package io.github.pulsebeat02.murderrun.data;

import io.github.pulsebeat02.murderrun.MurderRun;
import io.github.pulsebeat02.murderrun.data.hibernate.HibernateManager;
import io.github.pulsebeat02.murderrun.data.json.ArenaCreationDataJSONMapper;
import io.github.pulsebeat02.murderrun.data.json.ArenaDataJSONMapper;
import io.github.pulsebeat02.murderrun.data.json.LobbyDataJSONMapper;
import io.github.pulsebeat02.murderrun.data.json.StatisticJSONMapper;
import io.github.pulsebeat02.murderrun.data.yaml.ConfigurationManager;
import io.github.pulsebeat02.murderrun.data.yaml.PluginDataConfigurationMapper;
import io.github.pulsebeat02.murderrun.game.arena.ArenaManager;
import io.github.pulsebeat02.murderrun.game.lobby.LobbyManager;
import io.github.pulsebeat02.murderrun.game.statistics.StatisticsManager;
import io.github.pulsebeat02.murderrun.gui.arena.ArenaCreationManager;

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
