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

import io.github.pulsebeat02.murderrun.game.player.PlayerManager;
import io.github.pulsebeat02.murderrun.game.scheduler.GameScheduler;
import io.github.pulsebeat02.murderrun.locale.Message;
import java.util.UUID;
import net.kyori.adventure.bossbar.BossBar;
import net.kyori.adventure.text.Component;
import org.checkerframework.checker.initialization.qual.UnderInitialization;

public final class GameTimerUpdater {

  private final Game game;
  private final String id;

  public GameTimerUpdater(final Game game) {
    this.game = game;
    this.id = generateRandomID();
  }

  private String generateRandomID(@UnderInitialization GameTimerUpdater this) {
    final UUID uuid = UUID.randomUUID();
    return uuid.toString();
  }

  public void start() {
    setBossBars();
    final GameScheduler scheduler = this.game.getScheduler();
    scheduler.scheduleRepeatedTask(this::updateBossBars, 0, 20);
  }

  private void setBossBars() {
    final PlayerManager manager = this.game.getPlayerManager();
    final Component name = Message.BOSS_BAR.build();
    final BossBar.Color color = BossBar.Color.GREEN;
    final BossBar.Overlay overlay = BossBar.Overlay.NOTCHED_20;
    final float progress = 1.0f;
    manager.showBossBarForAllParticipants(id, name, progress, color, overlay);
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
    final PlayerManager manager = this.game.getPlayerManager();
    manager.updateBossBarForAllParticipants(id, progress);

    final int time = (int) timeLeft / 1000;
    manager.applyToAllParticipants(player -> player.setLevel(time));
  }
}
