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

  private void checkForKillerWin(final long timeLeft) {
    if (timeLeft <= 0) {
      this.game.finishGame(GameResult.MURDERERS);
    }
  }

  private void updateBossBars() {
    final GameTimer timer = this.game.getTimeManager();
    final long timeLeft = timer.getTimeLeft();
    checkForKillerWin(timeLeft);

    final long total = timer.getTotalTime();
    final float progress = (float) timeLeft / total;
    final PlayerManager manager = this.game.getPlayerManager();
    manager.updateBossBarForAllParticipants(id, progress);

    final int time = (int) timeLeft / 1000;
    manager.applyToAllParticipants(player -> player.setLevel(time));
  }
}
