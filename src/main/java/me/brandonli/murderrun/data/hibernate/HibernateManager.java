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
package me.brandonli.murderrun.data.hibernate;

import me.brandonli.murderrun.MurderRun;
import me.brandonli.murderrun.data.hibernate.controllers.ArenaController;
import me.brandonli.murderrun.data.hibernate.controllers.ArenaCreationController;
import me.brandonli.murderrun.data.hibernate.controllers.LobbyController;
import me.brandonli.murderrun.data.hibernate.controllers.StatisticsController;
import me.brandonli.murderrun.data.hibernate.identifier.HibernateIdentifierManager;
import me.brandonli.murderrun.data.yaml.PluginDataConfigurationMapper;
import me.brandonli.murderrun.game.arena.Arena;
import me.brandonli.murderrun.game.arena.ArenaManager;
import me.brandonli.murderrun.game.lobby.Lobby;
import me.brandonli.murderrun.game.lobby.LobbyManager;
import me.brandonli.murderrun.game.statistics.PlayerStatistics;
import me.brandonli.murderrun.game.statistics.StatisticsManager;
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

  private SessionFactory constructSessionFactory(
      @UnderInitialization HibernateManager this, final MurderRun plugin) {
    try (final HibernateContextCloseable closeable = new HibernateContextCloseable()) {
      closeable.setContextClassLoader();
      final PluginDataConfigurationMapper mapper = plugin.getConfiguration();
      return this.constructSession(mapper);
    } catch (final HibernateException e) {
      throw new AssertionError(e);
    }
  }

  private SessionFactory constructSession(
      @UnderInitialization HibernateManager this, final PluginDataConfigurationMapper mapper) {
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
