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
package me.brandonli.murderrun.game.player;

import me.brandonli.murderrun.game.Game;
import me.brandonli.murderrun.game.GameProperties;
import me.brandonli.murderrun.game.scheduler.GameScheduler;
import me.brandonli.murderrun.game.scheduler.reference.NullReference;

public final class SprintManager {

  private final Game game;

  public SprintManager(final Game game) {
    this.game = game;
  }

  public void start() {
    this.startSurvivorSprintingCheckTask();
    this.startKillerSprintingCheckTask();
  }

  public void startSurvivorSprintingCheckTask() {
    final GamePlayerManager manager = this.game.getPlayerManager();
    final GameScheduler scheduler = this.game.getScheduler();
    final NullReference reference = NullReference.of();
    final int time = (int) (GameProperties.SURVIVOR_SPRINT_TIME * 20);
    final int period = time / 14;
    scheduler.scheduleRepeatedTask(() -> manager.applyToLivingSurvivors(this::applySprintLogic), 1L, period, reference);
  }

  public void startKillerSprintingCheckTask() {
    final GamePlayerManager manager = this.game.getPlayerManager();
    final GameScheduler scheduler = this.game.getScheduler();
    final NullReference reference = NullReference.of();
    final int time = (int) (GameProperties.KILLER_SPRINT_TIME * 20);
    final int period = time / 14;
    scheduler.scheduleRepeatedTask(() -> manager.applyToLivingKillers(this::applySprintLogic), 1L, period, reference);
  }

  private void applySprintLogic(final GamePlayer killer) {
    final boolean sprinting = killer.isSprinting();
    final int level = killer.getFoodLevel();
    if (sprinting && level > 6) {
      killer.setFoodLevel(level - 1);
    } else if (level < 20) {
      killer.setFoodLevel(level + 1);
    }
  }
}
