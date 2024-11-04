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
