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
package io.github.pulsebeat02.murderrun.data.hibernate;

import io.github.pulsebeat02.murderrun.MurderRun;
import io.github.pulsebeat02.murderrun.data.hibernate.controllers.ArenaController;
import io.github.pulsebeat02.murderrun.data.hibernate.controllers.ArenaCreationController;
import io.github.pulsebeat02.murderrun.data.hibernate.controllers.LobbyController;
import io.github.pulsebeat02.murderrun.data.hibernate.controllers.StatisticsController;
import io.github.pulsebeat02.murderrun.data.hibernate.identifier.HibernateIdentifierManager;
import io.github.pulsebeat02.murderrun.data.yaml.PluginDataConfigurationMapper;
import io.github.pulsebeat02.murderrun.game.arena.Arena;
import io.github.pulsebeat02.murderrun.game.arena.ArenaManager;
import io.github.pulsebeat02.murderrun.game.lobby.Lobby;
import io.github.pulsebeat02.murderrun.game.lobby.LobbyManager;
import io.github.pulsebeat02.murderrun.game.statistics.PlayerStatistics;
import io.github.pulsebeat02.murderrun.game.statistics.StatisticsManager;
import org.checkerframework.checker.initialization.qual.UnderInitialization;
import org.hibernate.HibernateException;
import org.hibernate.SessionFactory;
import org.hibernate.cfg.Configuration;
import org.hibernate.cfg.Environment;

public final class HibernateManager {

  private final MurderRun plugin;
  private final HibernateIdentifierManager manager;
  private final ArenaController arenaController;
  private final LobbyController lobbyController;
  private final StatisticsController statisticsController;
  private final ArenaCreationController arenaCreationController;
  private final SessionFactory factory;

  public HibernateManager(final MurderRun plugin) {
    this.plugin = plugin;
    this.manager = new HibernateIdentifierManager();
    this.factory = this.constructSessionFactory(plugin);
    this.arenaController = new ArenaController(this.manager, this.factory);
    this.lobbyController = new LobbyController(this.manager, this.factory);
    this.statisticsController = new StatisticsController(this.manager, this.factory);
    this.arenaCreationController = new ArenaCreationController(this.manager, this.factory);
  }

  public void shutdown() {
    this.arenaController.shutdown();
    this.lobbyController.shutdown();
    this.statisticsController.shutdown();
    this.arenaCreationController.shutdown();
    this.manager.shutdown();
    if (this.factory.isOpen()) {
      this.factory.close();
    }
  }

  private SessionFactory constructSessionFactory(@UnderInitialization HibernateManager this, final MurderRun plugin) {
    try (final HibernateContextCloseable closeable = new HibernateContextCloseable()) {
      closeable.setContextClassLoader();
      final PluginDataConfigurationMapper mapper = plugin.getConfiguration();
      return this.constructSession(mapper);
    } catch (final HibernateException e) {
      throw new AssertionError("Failed to connect to database!", e);
    }
  }

  private SessionFactory constructSession(@UnderInitialization HibernateManager this, final PluginDataConfigurationMapper mapper) {
    return new Configuration()
      .setProperty(Environment.JAKARTA_JDBC_DRIVER, mapper.getDatabaseDriver())
      .setProperty(Environment.JAKARTA_JDBC_USER, mapper.getDatabaseUsername())
      .setProperty(Environment.JAKARTA_JDBC_PASSWORD, mapper.getDatabasePassword())
      .setProperty(Environment.HBM2DDL_AUTO, mapper.getDatabaseHbm2ddl())
      .setProperty(Environment.SHOW_SQL, mapper.isDatabaseShowSql())
      .setProperty(Environment.JAKARTA_JDBC_URL, mapper.getDatabaseUrl())
      .setProperty(Environment.AUTOCOMMIT, true)
      .setProperty(Environment.AUTO_CLOSE_SESSION, true)
      .addAnnotatedClass(ArenaManager.class)
      .addAnnotatedClass(LobbyManager.class)
      .addAnnotatedClass(StatisticsManager.class)
      .addAnnotatedClass(Arena.class)
      .addAnnotatedClass(Lobby.class)
      .addAnnotatedClass(PlayerStatistics.class)
      .buildSessionFactory();
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

  public ArenaCreationController getArenaCreationController() {
    return this.arenaCreationController;
  }

  public SessionFactory getFactory() {
    return this.factory;
  }
}
