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
package me.brandonli.murderrun.game;

import java.util.UUID;
import me.brandonli.murderrun.game.player.GamePlayerManager;
import me.brandonli.murderrun.game.scheduler.GameScheduler;
import me.brandonli.murderrun.game.scheduler.reference.NullReference;
import me.brandonli.murderrun.locale.Message;
import net.kyori.adventure.bossbar.BossBar;
import net.kyori.adventure.text.Component;
import org.checkerframework.checker.initialization.qual.UnderInitialization;

public final class GameTimerUpdater {

  private final Game game;
  private final String id;

  public GameTimerUpdater(final Game game) {
    this.game = game;
    this.id = this.generateRandomID();
  }

  private String generateRandomID(@UnderInitialization GameTimerUpdater this) {
    final UUID uuid = UUID.randomUUID();
    return uuid.toString();
  }

  public void start() {
    this.setBossBars();
    final NullReference reference = NullReference.of();
    final GameScheduler scheduler = this.game.getScheduler();
    scheduler.scheduleRepeatedTask(this::updateBossBars, 0, 20, reference);
  }

  private void setBossBars() {
    final GamePlayerManager manager = this.game.getPlayerManager();
    final Component name = Message.BOSS_BAR.build();
    final BossBar.Color color = BossBar.Color.GREEN;
    final BossBar.Overlay overlay = BossBar.Overlay.NOTCHED_20;
    final float progress = 1.0f;
    manager.showBossBarForAllParticipants(this.id, name, progress, color, overlay);
  }

  private void updateBossBars() {
    final GameTimer timer = this.game.getTimeManager();
    final long timeLeft = timer.getTimeLeft();
    if (timeLeft <= 0) {
      this.game.finishGame(GameResult.MURDERERS);
      return;
    }

    final long total = timer.getTotalTime();
    final float progress = (float) timeLeft / total;
    final GamePlayerManager manager = this.game.getPlayerManager();
    manager.updateBossBarForAllParticipants(this.id, progress);

    final int time = (int) timeLeft / 1000;
    manager.applyToAllParticipants(player -> player.setLevel(time));
  }
}
