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

import java.util.function.BooleanSupplier;
import me.brandonli.murderrun.game.Game;
import me.brandonli.murderrun.game.scheduler.reference.Reference;

public final class ConditionalTask extends GameScheduledTask {

  private final BooleanSupplier condition;

  public ConditionalTask(final Game game, final Runnable runnable, final BooleanSupplier condition, final Reference<?> reference) {
    super(game, runnable, reference);
    this.condition = condition;
  }

  @Override
  public void run() {
    super.run();
    if (this.condition.getAsBoolean()) {
      this.cancel();
    }
  }
}
