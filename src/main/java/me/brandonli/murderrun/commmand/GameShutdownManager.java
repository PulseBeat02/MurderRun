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
package me.brandonli.murderrun.commmand;

import java.util.Collection;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicBoolean;
import me.brandonli.murderrun.MurderRun;
import me.brandonli.murderrun.game.Game;
import me.brandonli.murderrun.game.GameResult;
import me.brandonli.murderrun.game.GameStatus;
import me.brandonli.murderrun.game.lobby.PreGameManager;

public final class GameShutdownManager {

  private final Collection<PreGameManager> currentGames;

  public GameShutdownManager() {
    this.currentGames = ConcurrentHashMap.newKeySet();
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
