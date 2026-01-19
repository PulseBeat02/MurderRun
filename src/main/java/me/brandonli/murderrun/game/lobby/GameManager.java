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
package me.brandonli.murderrun.game.lobby;

import java.util.*;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicBoolean;
import me.brandonli.murderrun.MurderRun;
import me.brandonli.murderrun.commmand.GameShutdownManager;
import me.brandonli.murderrun.data.yaml.QuickJoinConfigurationMapper;
import me.brandonli.murderrun.game.*;
import me.brandonli.murderrun.game.arena.Arena;
import me.brandonli.murderrun.game.arena.ArenaManager;
import me.brandonli.murderrun.locale.AudienceProvider;
import me.brandonli.murderrun.locale.Message;
import me.brandonli.murderrun.utils.RandomUtils;
import net.kyori.adventure.audience.Audience;
import net.kyori.adventure.platform.bukkit.BukkitAudiences;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.checkerframework.checker.nullness.qual.Nullable;

public final class GameManager {

  private final MurderRun plugin;
  private final Map<String, PreGameManager> games;
  private final Map<UUID, PreGameManager> playerToGame;
  private final AtomicBoolean creation;

  public GameManager(final MurderRun plugin) {
    this.games = new ConcurrentHashMap<>();
    this.playerToGame = new ConcurrentHashMap<>();
    this.plugin = plugin;
    this.creation = new AtomicBoolean(false);
  }

  public @Nullable PreGameManager getGame(final String id) {
    return this.games.get(id);
  }

  public boolean leaveGame(final Player player) {
    final UUID uuid = player.getUniqueId();
    final PreGameManager preGameManager = this.playerToGame.remove(uuid);
    if (preGameManager != null) {
      final PreGamePlayerManager manager = preGameManager.getPlayerManager();
      manager.removeParticipantFromLobby(player);

      final Game game = preGameManager.getGame();
      final GameStatus status = game.getStatus();
      final GameStatus.Status actual = status.getStatus();
      if (actual != GameStatus.Status.NOT_STARTED) {
        player.setHealth(0);
      }

      return true;
    }

    return false;
  }

  public @Nullable PreGameManager getGameAsParticipant(final CommandSender participant) {
    if (participant instanceof final Player player) {
      final UUID uuid = player.getUniqueId();
      return this.playerToGame.get(uuid);
    }
    final Collection<PreGameManager> values = this.games.values();
    for (final PreGameManager manager : values) {
      final PreGamePlayerManager playerManager = manager.getPlayerManager();
      if (playerManager.hasPlayer(participant)) {
        return manager;
      }
    }
    return null;
  }

  public @Nullable PreGameManager getGame(final CommandSender target) {
    final Collection<PreGameManager> values = this.games.values();
    for (final PreGameManager manager : values) {
      final PreGamePlayerManager playerManager = manager.getPlayerManager();
      if (playerManager.hasPlayer(target)) {
        return manager;
      }
    }
    return null;
  }

  public synchronized boolean joinGame(final Player player, final String id) {
    final PreGameManager manager = this.games.get(id);
    if (manager != null) {
      final PreGamePlayerManager playerManager = manager.getPlayerManager();
      if (!playerManager.isGameFull() && !playerManager.isLocked()) {
        playerManager.addParticipantToLobby(player, false);
        final UUID uuid = player.getUniqueId();
        this.playerToGame.put(uuid, manager);
        return true;
      }
    }
    return false;
  }

  public void createGame(
      final CommandSender leader,
      final String id,
      final GameMode mode,
      final String arenaName,
      final String lobbyName,
      final int min,
      final int max,
      final boolean quickJoinable) {
    final GameEventsListener listener = new GameEventsPlayerListener(this);
    final PreGameManager game = this.createClampedGame(
        leader, id, mode, arenaName, lobbyName, min, max, quickJoinable, listener);
    this.addGameToRegistry(id, game);
    this.autoJoinIfLeaderPlayer(leader, id);
  }

  private PreGameManager createClampedGame(
      final CommandSender leader,
      final String id,
      final GameMode mode,
      final String arenaName,
      final String lobbyName,
      final int min,
      final int max,
      final boolean quickJoinable,
      final GameEventsListener listener) {
    this.sendGameCreationMessage(leader);
    final int finalMin = Math.clamp(min, 2, Integer.MAX_VALUE);
    final int finalMax = Math.clamp(max, finalMin, Integer.MAX_VALUE);
    final PreGameManager manager = new PreGameManager(this.plugin, this, id, mode, listener);
    this.setSettings(manager, arenaName, lobbyName);
    manager.initialize(leader, finalMin, finalMax, quickJoinable);
    return manager;
  }

  private void sendGameCreationMessage(final CommandSender leader) {
    final AudienceProvider provider = this.plugin.getAudience();
    final BukkitAudiences audiences = provider.retrieve();
    final Audience audience = audiences.sender(leader);
    audience.sendMessage(Message.GAME_CREATION_LOAD.build());
  }

  private synchronized void addGameToRegistry(final String id, final PreGameManager manager) {
    final GameShutdownManager shutdownManager = this.plugin.getGameShutdownManager();
    if (this.games.containsKey(id)) {
      this.removeGame(id);
    }
    this.games.put(id, manager);
    shutdownManager.addGame(manager);
  }

  private void autoJoinIfLeaderPlayer(final CommandSender leader, final String id) {
    if (leader instanceof final Player player) {
      this.joinGame(player, id);
    }
  }

  private void setSettings(final PreGameManager manager, final String arena, final String lobby) {
    final ArenaManager arenaManager = this.plugin.getArenaManager();
    final LobbyManager lobbyManager = this.plugin.getLobbyManager();
    final Arena arenaObj = arenaManager.getArena(arena);
    final Lobby lobbyObj = lobbyManager.getLobby(lobby);
    final GameSettings settings = manager.getSettings();
    settings.setArena(arenaObj);
    settings.setLobby(lobbyObj);
  }

  public synchronized void removeGame(final String id) {
    final PreGameManager manager = this.games.get(id);
    if (manager != null) {
      final PreGamePlayerManager playerManager = manager.getPlayerManager();
      final Collection<Player> participants = playerManager.getParticipants();
      for (final Player player : participants) {
        final UUID uuid = player.getUniqueId();
        this.playerToGame.remove(uuid);
      }
      final Game game = manager.getGame();
      game.finishGame(GameResult.INTERRUPTED);
      playerManager.forceShutdown();
      manager.shutdown(true);
      this.games.remove(id);
      final GameShutdownManager shutdownManager = this.plugin.getGameShutdownManager();
      shutdownManager.removeGame(manager);
    }
  }

  public boolean quickJoinGame(final Player player) {
    final QuickJoinConfigurationMapper config = this.plugin.getQuickJoinConfiguration();
    final AudienceProvider provider = this.plugin.getAudience();
    final BukkitAudiences audiences = provider.retrieve();
    final Audience audience = audiences.sender(player);
    if (!config.isEnabled()) {
      audience.sendMessage(Message.GAME_NONE.build());
      return false;
    }

    final Collection<PreGameManager> values = this.games.values();
    for (final PreGameManager manager : values) {
      final PreGamePlayerManager preGamePlayerManager = manager.getPlayerManager();
      final boolean join = preGamePlayerManager.isQuickJoinable()
          && !preGamePlayerManager.isGameFull()
          && !preGamePlayerManager.isLocked();
      if (join) {
        final String id = manager.getId();
        this.joinGame(player, id);
        return true;
      }
    }

    if (!this.creation.compareAndSet(false, true)) {
      audience.sendMessage(Message.GAME_QUICKJOIN_CREATION_LOAD.build());
      return false;
    }

    try {
      final UUID random = UUID.randomUUID();
      final String raw = random.toString();
      final List<String[]> pairs = config.getLobbyArenaPairs();
      if (pairs.isEmpty()) {
        audience.sendMessage(Message.GAME_NONE.build());
        return false;
      }

      final String[] rand = RandomUtils.getRandomElement(pairs);
      final String arena = rand[0];
      final String lobby = rand[1];
      final int min = config.getMinPlayers();
      final int max = config.getMaxPlayers();

      final GameMode[] modes = config.getGameModes();
      final GameMode mode = RandomUtils.getRandomElement(modes);
      this.createGame(player, raw, mode, arena, lobby, min, max, true);

      return true;
    } finally {
      this.creation.set(false);
    }
  }

  public MurderRun getPlugin() {
    return this.plugin;
  }

  public Map<String, PreGameManager> getGames() {
    return this.games;
  }

  public Map<UUID, PreGameManager> getPlayerToGame() {
    return this.playerToGame;
  }
}
