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
package me.brandonli.murderrun.game.scheduler;

import me.brandonli.murderrun.game.Game;
import me.brandonli.murderrun.game.GameStatus;
import me.brandonli.murderrun.game.scheduler.reference.Reference;
import org.bukkit.scheduler.BukkitRunnable;

public class GameScheduledTask extends BukkitRunnable implements ScheduledTask {

  private final Game game;
  private final Runnable runnable;
  private final Reference<?> reference;

  public GameScheduledTask(final Game game, final Runnable runnable, final Reference<?> reference) {
    this.game = game;
    this.runnable = runnable;
    this.reference = reference;
  }

  @Override
  public void run() {
    final GameStatus status = this.game.getStatus();
    final GameStatus.Status gameStatus = status.getStatus();
    if (gameStatus == GameStatus.Status.FINISHED || this.reference.isInvalid()) {
      this.cancel();
      return;
    }
    this.runnable.run();
  }
}
