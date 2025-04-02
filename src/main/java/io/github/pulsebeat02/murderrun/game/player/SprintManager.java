/*

MIT License

Copyright (c) 2025 Brandon Li

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
package io.github.pulsebeat02.murderrun.game.player;

import io.github.pulsebeat02.murderrun.game.Game;
import io.github.pulsebeat02.murderrun.game.GameProperties;
import io.github.pulsebeat02.murderrun.game.scheduler.GameScheduler;
import io.github.pulsebeat02.murderrun.game.scheduler.reference.NullReference;

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
    if (sprinting) {
      killer.setFoodLevel(level - 1);
    } else if (level < 20) {
      killer.setFoodLevel(level + 1);
    }
  }
}
