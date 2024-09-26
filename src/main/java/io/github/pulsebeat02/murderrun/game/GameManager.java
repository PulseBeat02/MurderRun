package io.github.pulsebeat02.murderrun.game;

import io.github.pulsebeat02.murderrun.MurderRun;
import io.github.pulsebeat02.murderrun.commmand.GameShutdownManager;
import io.github.pulsebeat02.murderrun.game.lobby.PreGameManager;
import io.github.pulsebeat02.murderrun.game.lobby.PreGamePlayerManager;
import java.util.Collection;
import java.util.HashMap;
import java.util.Map;
import org.bukkit.entity.Player;
import org.bukkit.inventory.PlayerInventory;

public final class GameManager {

  private final MurderRun plugin;
  private final Map<String, PreGameManager> games;

  public GameManager(final MurderRun plugin) {
    this.games = new HashMap<>();
    this.plugin = plugin;
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

  public void createGame(
      final String id, final int min, final int max, final boolean quickJoinable) {
    final GameEventsListener listener = new GameEventsListenerImpl(this);
    final PreGameManager manager =
        new PreGameManager(this.plugin, id, min, max, quickJoinable, listener);
    final GameShutdownManager shutdownManager = this.plugin.getGameShutdownManager();
    final Game running = manager.getGame();
    this.games.put(id, manager);
    manager.initialize();
    shutdownManager.addGame(running);
  }

  public void removeGame(final String id) {
    final PreGameManager manager = this.games.get(id);
    if (manager != null) {
      final PreGamePlayerManager playerManager = manager.getManager();
      final Game game = manager.getGame();
      game.finishGame(GameResult.INTERRUPTED);
      this.resetPrePlayerManager(playerManager);
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

  private void resetPrePlayerManager(final PreGamePlayerManager manager) {
    final Collection<Player> participants = manager.getParticipants();
    for (final Player player : participants) {
      final PlayerInventory inventory = player.getInventory();
      inventory.clear();
      player.setLevel(0);
    }
    manager.shutdown();
  }

  public MurderRun getPlugin() {
    return this.plugin;
  }

  public Map<String, PreGameManager> getGames() {
    return this.games;
  }
}
