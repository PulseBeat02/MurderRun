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
package me.brandonli.murderrun.data.yaml;

import static java.util.Objects.requireNonNull;

import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantReadWriteLock;
import me.brandonli.murderrun.MurderRun;
import me.brandonli.murderrun.data.RelationalDataMethod;
import me.brandonli.murderrun.locale.Locale;
import me.brandonli.murderrun.resourcepack.provider.ProviderMethod;
import me.brandonli.murderrun.utils.ExecutorUtils;
import org.bukkit.configuration.file.FileConfiguration;

public final class PluginDataConfigurationMapper {

  private static final String PLUGIN_LANGUAGE = "language";
  private static final String PACK_PROVIDER_FIELD = "pack-provider";
  private static final String RELATIONAL_DATA_FIELD = "relational-data-provider";
  private static final String SERVER_PORT_FIELD = "server.port";
  private static final String SERVER_HOST_FIELD = "server.host-name";

  private static final String DATABASE_DRIVER_FIELD = "database-options.driver";
  private static final String DATABASE_URL_FIELD = "database-options.jdbc-url";
  private static final String DATABASE_NAME_FIELD = "database-options.database-name";
  private static final String DATABASE_HBM2DDL_FIELD = "database-options.hbm2ddl";
  private static final String DATABASE_USERNAME_FIELD = "database-options.username";
  private static final String DATABASE_PASSWORD_FIELD = "database-options.password";
  private static final String DATABASE_SHOW_SQL_FIELD = "database-options.show-sql";

  private final ExecutorService service;
  private final MurderRun plugin;
  private final Lock readLock;
  private final Lock writeLock;

  private Locale locale;
  private ProviderMethod providerMethod;
  private RelationalDataMethod relationalDataMethod;
  private String hostName;
  private int port;

  private String databaseDriver;
  private String databaseUrl;
  private String databaseName;
  private String databaseHbm2ddl;
  private String databaseUsername;
  private String databasePassword;
  private boolean databaseShowSql;

  public PluginDataConfigurationMapper(final MurderRun plugin) {
    final ReentrantReadWriteLock lock = new ReentrantReadWriteLock();
    this.plugin = plugin;
    this.readLock = lock.readLock();
    this.writeLock = lock.writeLock();
    this.service = Executors.newVirtualThreadPerTaskExecutor();
    this.plugin.saveDefaultConfig();
  }

  public synchronized void shutdown() {
    ExecutorUtils.shutdownExecutorGracefully(this.service);
  }

  public synchronized MurderRun getPlugin() {
    return this.plugin;
  }

  public synchronized void deserialize() {
    this.readLock.lock();
    final FileConfiguration config = this.plugin.getConfig();
    this.plugin.saveConfig();
    this.locale = this.getLocale(config);
    this.hostName = this.getHostName(config);
    this.port = this.getPortServerPort(config);
    this.providerMethod = this.getProviderMethod(config);
    this.relationalDataMethod = this.getRelationalDataMethod(config);
    this.databaseDriver = this.getDatabaseDriver(config);
    this.databaseUrl = this.getDatabaseUrl(config);
    this.databaseName = this.getDatabaseName(config);
    this.databaseHbm2ddl = this.getDatabaseHbm2ddl(config);
    this.databaseUsername = this.getDatabaseUsername(config);
    this.databasePassword = this.getDatabasePassword(config);
    this.databaseShowSql = this.getDatabaseShowSql(config);
    this.readLock.unlock();
  }

  private Locale getLocale(final FileConfiguration config) {
    return Locale.fromString(requireNonNull(config.getString(PLUGIN_LANGUAGE, "EN_US")));
  }

  private String getDatabaseDriver(final FileConfiguration config) {
    return requireNonNull(config.getString(DATABASE_DRIVER_FIELD, "com.mysql.cj.jdbc.Driver"));
  }

  private String getDatabaseUrl(final FileConfiguration config) {
    return requireNonNull(config.getString(DATABASE_URL_FIELD, "jdbc:h2:tcp://localhost/~/test"));
  }

  private String getDatabaseName(final FileConfiguration config) {
    return requireNonNull(config.getString(DATABASE_NAME_FIELD, "murderrun"));
  }

  private String getDatabaseHbm2ddl(final FileConfiguration config) {
    return requireNonNull(config.getString(DATABASE_HBM2DDL_FIELD, "update"));
  }

  private String getDatabaseUsername(final FileConfiguration config) {
    return requireNonNull(config.getString(DATABASE_USERNAME_FIELD, "as"));
  }

  private String getDatabasePassword(final FileConfiguration config) {
    return requireNonNull(config.getString(DATABASE_PASSWORD_FIELD, ""));
  }

  private boolean getDatabaseShowSql(final FileConfiguration config) {
    return config.getBoolean(DATABASE_SHOW_SQL_FIELD);
  }

  private int getPortServerPort(final FileConfiguration config) {
    return config.getInt(SERVER_PORT_FIELD);
  }

  private String getHostName(final FileConfiguration config) {
    return requireNonNull(config.getString(SERVER_HOST_FIELD, "localhost"));
  }

  private ProviderMethod getProviderMethod(final FileConfiguration config) {
    return ProviderMethod.fromString(
        requireNonNull(config.getString(PACK_PROVIDER_FIELD, "MC_PACK_HOSTING")));
  }

  private RelationalDataMethod getRelationalDataMethod(final FileConfiguration config) {
    return RelationalDataMethod.fromString(
        requireNonNull(config.getString(RELATIONAL_DATA_FIELD, "JSON")));
  }

  public synchronized Locale getLocale() {
    return this.locale;
  }

  public synchronized String getHostName() {
    return this.hostName;
  }

  public synchronized void serialize() {
    CompletableFuture.runAsync(this::internalSerialize, this.service);
  }

  private synchronized void internalSerialize() {
    this.writeLock.lock();
    final FileConfiguration config = this.plugin.getConfig();
    config.set(SERVER_HOST_FIELD, this.hostName);
    config.set(SERVER_PORT_FIELD, this.port);
    config.set(PACK_PROVIDER_FIELD, this.providerMethod.name());
    config.set(RELATIONAL_DATA_FIELD, this.relationalDataMethod.name());
    this.plugin.saveConfig();
    this.writeLock.unlock();
  }

  public synchronized boolean isDatabaseShowSql() {
    return this.databaseShowSql;
  }

  public synchronized String getDatabasePassword() {
    return this.databasePassword;
  }

  public synchronized String getDatabaseUsername() {
    return this.databaseUsername;
  }

  public synchronized String getDatabaseHbm2ddl() {
    return this.databaseHbm2ddl;
  }

  public synchronized String getDatabaseName() {
    return this.databaseName;
  }

  public synchronized String getDatabaseUrl() {
    return this.databaseUrl;
  }

  public synchronized String getDatabaseDriver() {
    return this.databaseDriver;
  }

  public synchronized int getPort() {
    return this.port;
  }

  public synchronized ProviderMethod getProviderMethod() {
    return this.providerMethod;
  }

  public synchronized RelationalDataMethod getRelationalDataMethod() {
    return this.relationalDataMethod;
  }
}
