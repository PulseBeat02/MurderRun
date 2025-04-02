/*

MIT License

Copyright (c) 2025 Brandon Li

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
package io.github.pulsebeat02.murderrun.data.yaml;

import static java.util.Objects.requireNonNull;

import io.github.pulsebeat02.murderrun.MurderRun;
import io.github.pulsebeat02.murderrun.game.arena.ArenaManager;
import io.github.pulsebeat02.murderrun.game.lobby.LobbyManager;
import io.github.pulsebeat02.murderrun.utils.ExecutorUtils;
import io.github.pulsebeat02.murderrun.utils.IOUtils;
import java.io.File;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.List;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantReadWriteLock;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;
import org.checkerframework.checker.initialization.qual.UnderInitialization;

public final class QuickJoinConfigurationMapper {

  private static final String ENABLED_FIELD = "enabled";
  private static final String MIN_PLAYERS_FIELD = "min-players";
  private static final String MAX_PLAYERS_FIELD = "max-players";
  private static final String ARENA_LOBBY_PAIRS_FIELD = "arena-lobby-pairs";
  private static final String CONFIGURATION_YAML = "quick-join.yml";

  private final ExecutorService service;
  private final MurderRun plugin;
  private final Lock readLock;
  private final Lock writeLock;

  private boolean enabled;
  private int minPlayers;
  private int maxPlayers;
  private List<String[]> lobbyArenaPairs;

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
    this.readLock.unlock();
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
    final List<String[]> result = requireNonNull(pairs)
      .stream()
      .map(pair -> pair.toArray(new String[0]))
      .filter(this::isValidParameters)
      .toList();
    if (this.enabled && result.isEmpty()) {
      this.enabled = false;
      throw new AssertionError("Please specify at least one arena-lobby pair in the quick-join.yml file.");
    }
    return result;
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
}
