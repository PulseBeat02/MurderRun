package io.github.pulsebeat02.murderrun.data.hibernate;

import io.github.pulsebeat02.murderrun.MurderRun;
import io.github.pulsebeat02.murderrun.data.hibernate.controllers.ArenaController;
import io.github.pulsebeat02.murderrun.data.hibernate.controllers.LobbyController;
import io.github.pulsebeat02.murderrun.data.hibernate.controllers.StatisticsController;
import io.github.pulsebeat02.murderrun.game.arena.ArenaManager;
import io.github.pulsebeat02.murderrun.game.lobby.LobbyManager;
import io.github.pulsebeat02.murderrun.game.statistics.StatisticsManager;
import io.github.pulsebeat02.murderrun.utils.IOUtils;
import java.io.File;
import java.nio.file.Path;
import org.checkerframework.checker.initialization.qual.UnderInitialization;
import org.hibernate.SessionFactory;
import org.hibernate.cfg.Configuration;

public final class HibernateManager {

  private static final String HIBERNATE_CONFIG_FILE_NAME = "hibernate.cfg.xml";

  private final MurderRun plugin;
  private final ArenaController arenaController;
  private final LobbyController lobbyController;
  private final StatisticsController statisticsController;
  private final SessionFactory factory;

  public HibernateManager(final MurderRun plugin) {
    this.plugin = plugin;
    this.factory = this.constructSessionFactory(plugin);
    this.arenaController = new ArenaController(this.factory);
    this.lobbyController = new LobbyController(this.factory);
    this.statisticsController = new StatisticsController(this.factory);
  }

  private SessionFactory constructSessionFactory(@UnderInitialization HibernateManager this, final MurderRun plugin) {
    plugin.saveResource(HIBERNATE_CONFIG_FILE_NAME, false);
    final Path configuration = this.getHibernateConfigPath();
    final File legacy = configuration.toFile();
    return new Configuration()
      .configure(legacy)
      .addAnnotatedClass(ArenaManager.class)
      .addAnnotatedClass(LobbyManager.class)
      .addAnnotatedClass(StatisticsManager.class)
      .buildSessionFactory();
  }

  private Path getHibernateConfigPath(@UnderInitialization HibernateManager this) {
    final Path data = IOUtils.getPluginDataFolderPath();
    return data.resolve(HIBERNATE_CONFIG_FILE_NAME);
  }

  public MurderRun getPlugin() {
    return this.plugin;
  }

  public ArenaController getArenaController() {
    return this.arenaController;
  }

  public LobbyController getLobbyController() {
    return this.lobbyController;
  }

  public StatisticsController getStatisticsController() {
    return this.statisticsController;
  }

  public SessionFactory getFactory() {
    return this.factory;
  }
}
