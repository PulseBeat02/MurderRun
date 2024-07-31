package io.github.pulsebeat02.murderrun.scheduler;

import io.github.pulsebeat02.murderrun.game.MurderGame;
import org.bukkit.scheduler.BukkitRunnable;

public sealed class MurderScheduler extends BukkitRunnable
    permits ConditionalTask, TemporaryRepeatedTask {

  private final MurderGame game;
  private final Runnable runnable;

  public MurderScheduler(final MurderGame game, final Runnable runnable) {
    this.game = game;
    this.runnable = runnable;
  }

  @Override
  public void run() {
    this.runnable.run();
    if (this.game.isFinished()) {
      this.cancel();
    }
  }
}
