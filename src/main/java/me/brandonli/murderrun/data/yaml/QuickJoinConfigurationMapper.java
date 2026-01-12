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

import java.io.File;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.List;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantReadWriteLock;
import me.brandonli.murderrun.MurderRun;
import me.brandonli.murderrun.game.GameMode;
import me.brandonli.murderrun.game.arena.ArenaManager;
import me.brandonli.murderrun.game.lobby.LobbyManager;
import me.brandonli.murderrun.utils.ExecutorUtils;
import me.brandonli.murderrun.utils.IOUtils;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;
import org.checkerframework.checker.initialization.qual.UnderInitialization;

public final class QuickJoinConfigurationMapper {

  private static final String ENABLED_FIELD = "enabled";
  private static final String MIN_PLAYERS_FIELD = "min-players";
  private static final String MAX_PLAYERS_FIELD = "max-players";
  private static final String ARENA_LOBBY_PAIRS_FIELD = "arena-lobby-pairs";
  private static final String GAME_MODES = "game-modes";
  private static final String CONFIGURATION_YAML = "quick-join.yml";

  private final ExecutorService service;
  private final MurderRun plugin;
  private final Lock readLock;
  private final Lock writeLock;

  private boolean enabled;
  private int minPlayers;
  private int maxPlayers;
  private List<String[]> lobbyArenaPairs;
  private GameMode[] gameModes;

  public QuickJoinConfigurationMapper(final MurderRun plugin) {
    final ReentrantReadWriteLock lock = new ReentrantReadWriteLock();
    this.plugin = plugin;
    this.readLock = lock.readLock();
    this.writeLock = lock.writeLock();
    this.service = Executors.newVirtualThreadPerTaskExecutor();
    this.saveConfiguration(plugin);
  }

  public void saveConfiguration(@UnderInitialization QuickJoinConfigurationMapper this, final MurderRun plugin) {
    final Path path = IOUtils.getPluginDataFolderPath();
    final Path configPath = path.resolve(CONFIGURATION_YAML);
    if (Files.notExists(configPath)) {
      plugin.saveResource(CONFIGURATION_YAML, false);
    }
  }

  public synchronized void shutdown() {
    ExecutorUtils.shutdownExecutorGracefully(this.service);
  }

  public synchronized MurderRun getPlugin() {
    return this.plugin;
  }

  public synchronized void deserialize() {
    this.readLock.lock();
    final Path path = IOUtils.getPluginDataFolderPath();
    final Path configPath = path.resolve(CONFIGURATION_YAML);
    final File configFile = configPath.toFile();
    final FileConfiguration config = YamlConfiguration.loadConfiguration(configFile);
    this.plugin.saveConfig();
    this.enabled = this.getEnabled(config);
    this.minPlayers = this.getMinPlayers(config);
    this.maxPlayers = this.getMaxPlayers(config);
    this.lobbyArenaPairs = this.getLobbyArenaPairs(config);
    this.gameModes = this.getGameModes(config);
    this.readLock.unlock();
  }

  public GameMode[] getGameModes(final FileConfiguration config) {
    final List<String> modes = config.getStringList(GAME_MODES);
    return modes.stream().map(GameMode::valueOf).toArray(GameMode[]::new);
  }

  private boolean getEnabled(final FileConfiguration config) {
    return config.getBoolean(ENABLED_FIELD, true);
  }

  private int getMinPlayers(final FileConfiguration config) {
    return config.getInt(MIN_PLAYERS_FIELD, 2);
  }

  private int getMaxPlayers(final FileConfiguration config) {
    return config.getInt(MAX_PLAYERS_FIELD, 16);
  }

  private List<String[]> getLobbyArenaPairs(final FileConfiguration config) {
    if (!this.enabled) {
      return List.of();
    }
    @SuppressWarnings("unchecked")
    final List<List<String>> pairs = (List<List<String>>) config.getList(ARENA_LOBBY_PAIRS_FIELD, List.of());
    return requireNonNull(pairs).stream().map(pair -> pair.toArray(new String[0])).filter(this::isValidParameters).toList();
  }

  public boolean isValidParameters(final String[] pair) {
    if (pair == null || pair.length != 2) {
      return false;
    }
    final String arena = pair[0];
    final String lobby = pair[1];
    final ArenaManager arenaManager = this.plugin.getArenaManager();
    final LobbyManager lobbyManager = this.plugin.getLobbyManager();
    return arenaManager.getArena(arena) != null && lobbyManager.getLobby(lobby) != null;
  }

  public synchronized void serialize() {
    CompletableFuture.runAsync(this::internalSerialize, this.service);
  }

  private void internalSerialize() {
    this.writeLock.lock();
    final List<List<String>> back = this.lobbyArenaPairs.stream().map(List::of).toList();
    final FileConfiguration config = this.plugin.getConfig();
    config.set(ENABLED_FIELD, this.enabled);
    config.set(MIN_PLAYERS_FIELD, this.minPlayers);
    config.set(MAX_PLAYERS_FIELD, this.maxPlayers);
    config.set(ARENA_LOBBY_PAIRS_FIELD, back);
    config.set(GAME_MODES, this.gameModes);
    this.plugin.saveConfig();
    this.writeLock.unlock();
  }

  public synchronized boolean isEnabled() {
    return this.enabled;
  }

  public synchronized int getMinPlayers() {
    return this.minPlayers;
  }

  public synchronized int getMaxPlayers() {
    return this.maxPlayers;
  }

  public synchronized List<String[]> getLobbyArenaPairs() {
    return this.lobbyArenaPairs;
  }

  public synchronized GameMode[] getGameModes() {
    return this.gameModes;
  }
}
