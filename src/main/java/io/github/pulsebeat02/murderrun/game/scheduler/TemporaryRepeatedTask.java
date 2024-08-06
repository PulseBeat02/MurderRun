package io.github.pulsebeat02.murderrun.game.scheduler;

import io.github.pulsebeat02.murderrun.game.Game;
import java.util.concurrent.atomic.AtomicLong;

public final class TemporaryRepeatedTask extends GameScheduledTask {

  private final AtomicLong time;

  public TemporaryRepeatedTask(
      final Game game, final Runnable runnable, final long period, final long duration) {
    super(game, runnable);
    final long count = (duration + period - 1) / period;
    this.time = new AtomicLong(count);
  }

  @Override
  public void run() {
    super.run();
    final long raw = this.time.decrementAndGet();
    if (raw <= 0) {
      this.cancel();
    }
  }
}
