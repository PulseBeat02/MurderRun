package io.github.pulsebeat02.murderrun.game.lobby;

import io.github.pulsebeat02.murderrun.MurderRun;
import io.github.pulsebeat02.murderrun.commmand.GameShutdownManager;
import io.github.pulsebeat02.murderrun.game.*;
import io.github.pulsebeat02.murderrun.game.arena.Arena;
import io.github.pulsebeat02.murderrun.game.arena.ArenaManager;
import java.util.Collection;
import java.util.HashMap;
import java.util.Map;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.checkerframework.checker.nullness.qual.Nullable;
import org.jetbrains.annotations.NotNull;

public final class GameManager {

  private final MurderRun plugin;
  private final Map<String, PreGameManager> games;

  public GameManager(final MurderRun plugin) {
    this.games = new HashMap<>();
    this.plugin = plugin;
  }

  public @Nullable PreGameManager getGameAsParticipant(final CommandSender participant) {
    final Collection<PreGameManager> values = this.games.values();
    for (final PreGameManager manager : values) {
      final PreGamePlayerManager playerManager = manager.getManager();
      if (playerManager.hasPlayer(participant)) {
        return manager;
      }
    }
    return null;
  }

  public @Nullable PreGameManager getGame(final CommandSender leader) {
    final Collection<PreGameManager> values = this.games.values();
    for (final PreGameManager manager : values) {
      final PreGamePlayerManager playerManager = manager.getManager();
      if (playerManager.isLeader(leader)) {
        return manager;
      }
    }
    return null;
  }

  public boolean joinGame(final Player player, final String id) {
    final PreGameManager manager = this.games.get(id);
    if (manager != null) {
      final PreGamePlayerManager playerManager = manager.getManager();
      if (playerManager.canJoinGame()) {
        playerManager.addParticipantToLobby(player, false);
        return true;
      }
    }
    return false;
  }

  public PreGameManager createGame(
      final CommandSender leader,
      final String id,
      final String arenaName,
      final String lobbyName,
      final int min,
      final int max,
      final boolean quickJoinable) {
    final GameEventsListener listener = new GameEventsListenerImpl(this);
    final PreGameManager manager =
        this.createClampedGame(leader, id, arenaName, lobbyName, min, max, quickJoinable, listener);
    this.addGameToRegistry(id, manager);
    this.addGameToRegistry(id, manager);
    this.autoJoinIfLeaderPlayer(leader, id);
    return manager;
  }

  private @NotNull PreGameManager createClampedGame(
      final CommandSender leader,
      final String id,
      final String arenaName,
      final String lobbyName,
      final int min,
      final int max,
      final boolean quickJoinable,
      final GameEventsListener listener) {
    final int finalMin = Math.clamp(min, 2, Integer.MAX_VALUE);
    final int finalMax = Math.clamp(max, finalMin, Integer.MAX_VALUE);
    final PreGameManager manager =
        new PreGameManager(this.plugin, leader, id, finalMin, finalMax, quickJoinable, listener);
    this.setSettings(manager, arenaName, lobbyName);
    manager.initialize();
    return manager;
  }

  private void addGameToRegistry(final String id, final PreGameManager manager) {
    final GameShutdownManager shutdownManager = this.plugin.getGameShutdownManager();
    final Game game = manager.getGame();
    if (this.games.containsKey(id)) {
      this.removeGame(id);
    }
    this.games.put(id, manager);
    shutdownManager.addGame(game);
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

  public void removeGame(final String id) {
    final PreGameManager manager = this.games.get(id);
    if (manager != null) {
      final PreGamePlayerManager playerManager = manager.getManager();
      final Game game = manager.getGame();
      game.finishGame(GameResult.INTERRUPTED);
      playerManager.shutdown();
      this.games.remove(id);
    }
  }

  public boolean quickJoinGame(final Player player) {
    final Collection<PreGameManager> values = this.games.values();
    for (final PreGameManager manager : values) {
      final PreGamePlayerManager preGamePlayerManager = manager.getManager();
      if (preGamePlayerManager.canJoinGame()) {
        preGamePlayerManager.addParticipantToLobby(player, false);
        return true;
      }
    }
    return false;
  }

  public MurderRun getPlugin() {
    return this.plugin;
  }

  public Map<String, PreGameManager> getGames() {
    return this.games;
  }
}
