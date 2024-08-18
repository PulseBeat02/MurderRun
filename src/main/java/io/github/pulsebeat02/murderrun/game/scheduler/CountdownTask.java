package io.github.pulsebeat02.murderrun.game.scheduler;

import io.github.pulsebeat02.murderrun.game.Game;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.function.Consumer;

public final class CountdownTask extends GameScheduledTask {

  private final AtomicInteger seconds;
  private final Consumer<Integer> tasks;

  public CountdownTask(
      final Game game, final Runnable runnable, final int seconds, final Consumer<Integer> tasks) {
    super(game, runnable);
    this.seconds = new AtomicInteger(seconds + 1);
    this.tasks = tasks;
  }

  @Override
  public void run() {
    super.run();
    final int seconds = this.seconds.decrementAndGet();
    this.tasks.accept(seconds);
    if (seconds == 0) {
      this.cancel();
    }
  }
}
