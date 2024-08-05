package io.github.pulsebeat02.murderrun.game.scheduler;

import java.util.concurrent.atomic.AtomicLong;
import org.bukkit.scheduler.BukkitRunnable;

public final class TrulyTemporaryRepeatedTask extends BukkitRunnable {

  private final Runnable runnable;
  private final AtomicLong time;

  public TrulyTemporaryRepeatedTask(
      final Runnable runnable, final long period, final long duration) {
    final long count = (duration + period - 1) / period;
    this.time = new AtomicLong(count);
    this.runnable = runnable;
  }

  @Override
  public void run() {
    this.runnable.run();
    final long raw = this.time.decrementAndGet();
    if (raw <= 0) {
      this.cancel();
    }
  }
}
