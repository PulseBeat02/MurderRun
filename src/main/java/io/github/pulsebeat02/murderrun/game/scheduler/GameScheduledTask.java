package io.github.pulsebeat02.murderrun.game.scheduler;

import io.github.pulsebeat02.murderrun.game.Game;
import org.bukkit.scheduler.BukkitRunnable;

public class GameScheduledTask extends BukkitRunnable implements ScheduledTask {

  private final Game game;
  private final Runnable runnable;

  public GameScheduledTask(final Game game, final Runnable runnable) {
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
