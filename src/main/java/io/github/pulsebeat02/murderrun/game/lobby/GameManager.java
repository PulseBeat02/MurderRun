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
package io.github.pulsebeat02.murderrun.game.lobby;

import io.github.pulsebeat02.murderrun.MurderRun;
import io.github.pulsebeat02.murderrun.commmand.GameShutdownManager;
import io.github.pulsebeat02.murderrun.data.yaml.QuickJoinConfigurationMapper;
import io.github.pulsebeat02.murderrun.game.*;
import io.github.pulsebeat02.murderrun.game.arena.Arena;
import io.github.pulsebeat02.murderrun.game.arena.ArenaManager;
import io.github.pulsebeat02.murderrun.locale.AudienceProvider;
import io.github.pulsebeat02.murderrun.locale.Message;
import io.github.pulsebeat02.murderrun.utils.RandomUtils;
import java.util.*;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.atomic.AtomicBoolean;
import net.kyori.adventure.audience.Audience;
import net.kyori.adventure.platform.bukkit.BukkitAudiences;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.checkerframework.checker.nullness.qual.Nullable;

public final class GameManager {

  private final MurderRun plugin;
  private final Map<String, PreGameManager> games;
  private final AtomicBoolean creation;

  public GameManager(final MurderRun plugin) {
    this.games = new HashMap<>();
    this.plugin = plugin;
    this.creation = new AtomicBoolean(false);
  }

  public @Nullable PreGameManager getGame(final String id) {
    return this.games.get(id);
  }

  public boolean leaveGame(final Player player) {
    final PreGameManager preGameManager = this.getGameAsParticipant(player);
    if (preGameManager != null) {
      final PreGamePlayerManager manager = preGameManager.getPlayerManager();
      manager.removeParticipantFromLobby(player);

      final Game game = preGameManager.getGame();
      final GameStatus status = game.getStatus();
      final GameStatus.Status actual = status.getStatus();
      if (actual != GameStatus.Status.NOT_STARTED) {
        player.setHealth(0.0);
      }

      return true;
    }

    return false;
  }

  public @Nullable PreGameManager getGameAsParticipant(final CommandSender participant) {
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

  public boolean joinGame(final Player player, final String id) {
    final PreGameManager manager = this.games.get(id);
    if (manager != null) {
      final PreGamePlayerManager playerManager = manager.getPlayerManager();
      if (!playerManager.isGameFull() && !playerManager.isLocked()) {
        playerManager.addParticipantToLobby(player, false);
        return true;
      }
    }
    return false;
  }

  public CompletableFuture<Void> createGame(
    final CommandSender leader,
    final String id,
    final String arenaName,
    final String lobbyName,
    final int min,
    final int max,
    final boolean quickJoinable
  ) {
    final GameEventsListener listener = new GameEventsPlayerListener(this);
    return this.createClampedGame(leader, id, arenaName, lobbyName, min, max, quickJoinable, listener)
      .thenAccept(manager -> this.addGameToRegistry(id, manager))
      .thenAccept(manager -> this.autoJoinIfLeaderPlayer(leader, id))
      .thenApply(manager -> manager);
  }

  private CompletableFuture<PreGameManager> createClampedGame(
    final CommandSender leader,
    final String id,
    final String arenaName,
    final String lobbyName,
    final int min,
    final int max,
    final boolean quickJoinable,
    final GameEventsListener listener
  ) {
    this.sendGameCreationMessage(leader);
    final int finalMin = Math.clamp(min, 2, Integer.MAX_VALUE);
    final int finalMax = Math.clamp(max, finalMin, Integer.MAX_VALUE);
    final PreGameManager manager = new PreGameManager(this.plugin, this, id, listener);
    this.setSettings(manager, arenaName, lobbyName);
    return manager.initialize(leader, finalMin, finalMax, quickJoinable).thenApply(ignored -> manager);
  }

  private void sendGameCreationMessage(final CommandSender leader) {
    final AudienceProvider provider = this.plugin.getAudience();
    final BukkitAudiences audiences = provider.retrieve();
    final Audience audience = audiences.sender(leader);
    audience.sendMessage(Message.GAME_CREATION_LOAD.build());
  }

  private void addGameToRegistry(final String id, final PreGameManager manager) {
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

  public void removeGame(final String id) {
    final PreGameManager manager = this.games.get(id);
    if (manager != null) {
      final PreGamePlayerManager playerManager = manager.getPlayerManager();
      final Game game = manager.getGame();
      game.finishGame(GameResult.INTERRUPTED);
      playerManager.forceShutdown();
      manager.shutdown(true);
      this.games.remove(id);
    }
  }

  public CompletableFuture<Boolean> quickJoinGame(final Player player) {
    final QuickJoinConfigurationMapper config = this.plugin.getQuickJoinConfiguration();
    final AudienceProvider provider = this.plugin.getAudience();
    final BukkitAudiences audiences = provider.retrieve();
    final Audience audience = audiences.sender(player);
    if (!config.isEnabled()) {
      audience.sendMessage(Message.GAME_NONE.build());
      return CompletableFuture.completedFuture(false);
    }

    final Collection<PreGameManager> values = this.games.values();
    for (final PreGameManager manager : values) {
      final PreGamePlayerManager preGamePlayerManager = manager.getPlayerManager();
      final boolean join = preGamePlayerManager.isQuickJoinable() && !preGamePlayerManager.isGameFull() && !preGamePlayerManager.isLocked();
      if (join) {
        final String id = manager.getId();
        this.joinGame(player, id);
        return CompletableFuture.completedFuture(true);
      }
    }

    if (this.creation.get()) {
      audience.sendMessage(Message.GAME_QUICKJOIN_CREATION_LOAD.build());
      return CompletableFuture.completedFuture(false);
    }
    this.creation.set(true);

    final UUID random = UUID.randomUUID();
    final String raw = random.toString();
    final List<String[]> pairs = config.getLobbyArenaPairs();
    final String[] rand = RandomUtils.getRandomElement(pairs);
    final String arena = rand[0];
    final String lobby = rand[1];
    final int min = config.getMinPlayers();
    final int max = config.getMaxPlayers();

    return this.createGame(player, raw, arena, lobby, min, max, true)
      .thenRun(() -> this.creation.set(false))
      .thenApply(manager -> true)
      .exceptionally(e -> {
        this.creation.set(false);
        throw new AssertionError(e);
      });
  }

  public MurderRun getPlugin() {
    return this.plugin;
  }

  public Map<String, PreGameManager> getGames() {
    return this.games;
  }
}
