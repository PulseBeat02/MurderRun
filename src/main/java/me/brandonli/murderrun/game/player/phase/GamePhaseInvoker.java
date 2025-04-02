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
package me.brandonli.murderrun.game.player.phase;

import me.brandonli.murderrun.game.Game;
import me.brandonli.murderrun.game.GameResult;
import me.brandonli.murderrun.game.stage.GameCleanupTool;
import me.brandonli.murderrun.game.stage.GameStartupTool;

public final class GamePhaseInvoker {

  private final Game game;

  public GamePhaseInvoker(final Game game) {
    this.game = game;
  }

  public void invokeStartup() {
    final GameStartupTool startup = new GameStartupTool(this.game);
    startup.start();
  }

  public void invokeCleanup(final GameResult code) {
    final GameCleanupTool cleanup = new GameCleanupTool(this.game);
    cleanup.start(code);
  }
}
