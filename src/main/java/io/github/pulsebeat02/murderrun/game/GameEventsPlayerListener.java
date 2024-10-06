package io.github.pulsebeat02.murderrun.game;

import io.github.pulsebeat02.murderrun.MurderRun;
import io.github.pulsebeat02.murderrun.commmand.GameShutdownManager;
import io.github.pulsebeat02.murderrun.game.lobby.GameManager;
import io.github.pulsebeat02.murderrun.game.lobby.PreGameManager;
import java.util.Collection;
import java.util.Map;
import org.checkerframework.checker.nullness.qual.KeyFor;

public final class GameEventsPlayerListener implements GameEventsListener {

  private final GameManager manager;

  public GameEventsPlayerListener(final GameManager manager) {
    this.manager = manager;
  }

  @Override
  public void onGameFinish(final Game game, final GameResult result) {

    final MurderRun plugin = this.manager.getPlugin();
    final GameShutdownManager manager = plugin.getGameShutdownManager();
    manager.removeGame(game);

    final Map<String, PreGameManager> games = this.manager.getGames();
    final Collection<Map.Entry<@KeyFor("games") String, PreGameManager>> entries = games.entrySet();
    for (final Map.Entry<String, PreGameManager> entry : entries) {
      final PreGameManager pre = entry.getValue();
      final Game game1 = pre.getGame();
      if (game == game1) {
        final String id = entry.getKey();
        games.remove(id);
        break;
      }
    }
  }

  @Override
  public void onGameStart(final Game game) {}
}
