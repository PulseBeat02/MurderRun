package io.github.pulsebeat02.murderrun.data.hibernate;

import io.github.pulsebeat02.murderrun.MurderRun;
import io.github.pulsebeat02.murderrun.data.hibernate.controllers.ArenaController;
import io.github.pulsebeat02.murderrun.data.hibernate.controllers.LobbyController;
import io.github.pulsebeat02.murderrun.data.hibernate.controllers.StatisticsController;
import io.github.pulsebeat02.murderrun.game.arena.ArenaManager;
import io.github.pulsebeat02.murderrun.game.gadget.GadgetDataBundle;
import io.github.pulsebeat02.murderrun.game.lobby.LobbyManager;
import io.github.pulsebeat02.murderrun.game.statistics.StatisticsManager;
import io.github.pulsebeat02.murderrun.utils.IOUtils;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardCopyOption;
import java.util.PropertyResourceBundle;
import java.util.ResourceBundle;

import it.unimi.dsi.fastutil.io.FastBufferedInputStream;
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
    this.factory = this.constructSessionFactory();
    this.arenaController = new ArenaController(this.factory);
    this.lobbyController = new LobbyController(this.factory);
    this.statisticsController = new StatisticsController(this.factory);
  }

  private SessionFactory constructSessionFactory(@UnderInitialization HibernateManager this) {
    final Path configuration = this.getHibernateConfigPath();
    final File legacy = configuration.toFile();
    return new Configuration()
      .configure(legacy)
      .addAnnotatedClass(ArenaManager.class)
      .addAnnotatedClass(LobbyManager.class)
      .addAnnotatedClass(StatisticsManager.class)
      .buildSessionFactory();
  }

  private void checkExistence(@UnderInitialization HibernateManager this, final Path resourcePath) {
    if (IOUtils.createFile(resourcePath)) {
      try (final InputStream in = IOUtils.getResourceAsStream(HIBERNATE_CONFIG_FILE_NAME)) {
        Files.copy(in, resourcePath, StandardCopyOption.REPLACE_EXISTING);
      } catch (final IOException e) {
        throw new AssertionError(e);
      }
    }
  }

  private Path getHibernateConfigPath(@UnderInitialization HibernateManager this) {
    final Path data = IOUtils.getPluginDataFolderPath();
    final Path file = data.resolve(HIBERNATE_CONFIG_FILE_NAME);
    this.checkExistence(file);
    return file;
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
