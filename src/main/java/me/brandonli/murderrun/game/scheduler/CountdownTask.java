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

import java.util.concurrent.atomic.AtomicInteger;
import java.util.function.Consumer;
import me.brandonli.murderrun.game.Game;
import me.brandonli.murderrun.game.scheduler.reference.Reference;

public final class CountdownTask extends GameScheduledTask {

  private final AtomicInteger seconds;
  private final Consumer<Integer> tasks;

  public CountdownTask(
      final Game game,
      final Runnable runnable,
      final int seconds,
      final Consumer<Integer> tasks,
      final Reference<?> reference) {
    super(game, runnable, reference);
    this.seconds = new AtomicInteger(seconds + 1);
    this.tasks = tasks;
  }

  @Override
  public void run() {
    super.run();
    final int seconds = this.seconds.decrementAndGet();
    this.tasks.accept(seconds);
    if (seconds <= 0) {
      this.cancel();
    }
  }
}
