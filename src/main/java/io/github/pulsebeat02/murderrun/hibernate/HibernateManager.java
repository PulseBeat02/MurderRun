package io.github.pulsebeat02.murderrun.hibernate;

import io.github.pulsebeat02.murderrun.MurderRun;
import io.github.pulsebeat02.murderrun.game.arena.ArenaManager;
import io.github.pulsebeat02.murderrun.game.lobby.LobbyManager;
import io.github.pulsebeat02.murderrun.game.statistics.StatisticsManager;
import io.github.pulsebeat02.murderrun.hibernate.controllers.ArenaController;
import io.github.pulsebeat02.murderrun.hibernate.controllers.LobbyController;
import io.github.pulsebeat02.murderrun.hibernate.controllers.StatisticsController;
import io.github.pulsebeat02.murderrun.utils.IOUtils;
import java.io.File;
import java.nio.file.Path;
import org.hibernate.SessionFactory;
import org.hibernate.cfg.Configuration;

public final class HibernateManager {

  private static final String HIBERNATE_CONFIG_FILE_NAME = "hibernate.cfg.xml";

  private final MurderRun plugin;

  private ArenaController arenaController;
  private LobbyController lobbyController;
  private StatisticsController statisticsController;

  private SessionFactory factory;

  public HibernateManager(final MurderRun plugin) {
    this.plugin = plugin;
  }

  public void createSession() {
    final Path configuration = this.getHibernateConfigPath();
    final File legacy = configuration.toFile();
    this.plugin.saveResource(HIBERNATE_CONFIG_FILE_NAME, false);
    this.factory = new Configuration()
        .configure(legacy)
        .addAnnotatedClass(ArenaManager.class)
        .addAnnotatedClass(LobbyManager.class)
        .addAnnotatedClass(StatisticsManager.class)
        .buildSessionFactory();
    this.arenaController = new ArenaController(this.factory);
    this.lobbyController = new LobbyController(this.factory);
    this.statisticsController = new StatisticsController(this.factory);
  }

  private Path getHibernateConfigPath() {
    final Path data = IOUtils.getPluginDataFolderPath();
    return data.resolve(HIBERNATE_CONFIG_FILE_NAME);
  }
}
