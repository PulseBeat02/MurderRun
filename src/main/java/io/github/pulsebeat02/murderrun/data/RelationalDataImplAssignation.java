package io.github.pulsebeat02.murderrun.data;

import io.github.pulsebeat02.murderrun.MurderRun;
import io.github.pulsebeat02.murderrun.data.hibernate.HibernateManager;
import io.github.pulsebeat02.murderrun.data.json.ArenaDataJSONMapper;
import io.github.pulsebeat02.murderrun.data.json.LobbyDataJSONMapper;
import io.github.pulsebeat02.murderrun.data.yaml.ConfigurationManager;
import io.github.pulsebeat02.murderrun.data.yaml.PluginDataConfigurationMapper;
import io.github.pulsebeat02.murderrun.game.arena.ArenaManager;
import io.github.pulsebeat02.murderrun.game.lobby.LobbyManager;
import io.github.pulsebeat02.murderrun.game.statistics.StatisticJSONMapper;
import io.github.pulsebeat02.murderrun.game.statistics.StatisticsManager;

public final class RelationalDataImplAssignation {

  private final MurderRun plugin;
  private HibernateManager hibernate;
  private ConfigurationManager<ArenaManager> arenas;
  private ConfigurationManager<LobbyManager> lobbies;
  private ConfigurationManager<StatisticsManager> statistics;

  public RelationalDataImplAssignation(final MurderRun plugin) {
    this.plugin = plugin;
  }

  public void assignImplementations() {
    final PluginDataConfigurationMapper mapper = this.plugin.getConfiguration();
    final RelationalDataMethod method = mapper.getRelationalDataMethod();
    switch (method) {
      case JSON:
        this.arenas = new ArenaDataJSONMapper();
        this.lobbies = new LobbyDataJSONMapper();
        this.statistics = new StatisticJSONMapper();
        break;
      case SQL:
        this.hibernate = new HibernateManager(this.plugin);
        this.hibernate.createSession();
        this.arenas = this.hibernate.getArenaController();
        this.lobbies = this.hibernate.getLobbyController();
        this.statistics = this.hibernate.getStatisticsController();
        break;
      default:
        throw new UnsupportedOperationException("Unsupported provider method!");
    }
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
}
