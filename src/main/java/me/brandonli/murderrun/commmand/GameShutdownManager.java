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
package me.brandonli.murderrun.commmand;

import java.util.Collection;
import java.util.HashSet;
import java.util.concurrent.atomic.AtomicBoolean;
import me.brandonli.murderrun.MurderRun;
import me.brandonli.murderrun.game.Game;
import me.brandonli.murderrun.game.GameResult;
import me.brandonli.murderrun.game.GameStatus;
import me.brandonli.murderrun.game.lobby.PreGameManager;

public final class GameShutdownManager {

  private final Collection<PreGameManager> currentGames;

  public GameShutdownManager() {
    this.currentGames = new HashSet<>();
  }

  public void forceShutdown() {
    for (final PreGameManager preGameManager : this.currentGames) {
      final Game game = preGameManager.getGame();
      final GameStatus status = game.getStatus();
      final GameStatus.Status gameStatus = status.getStatus();
      if (gameStatus == GameStatus.Status.NOT_STARTED) {
        preGameManager.shutdown(true);
        continue;
      }
      game.finishGame(GameResult.INTERRUPTED);
    }
    this.currentGames.clear();
  }

  public void addGame(final PreGameManager game) {
    this.currentGames.add(game);
  }

  public void removeGame(final PreGameManager game) {
    final MurderRun plugin = game.getPlugin();
    final AtomicBoolean disabling = plugin.isDisabling();
    if (disabling.get()) {
      this.currentGames.remove(game);
    }
  }
}
