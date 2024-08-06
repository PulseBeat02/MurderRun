package io.github.pulsebeat02.murderrun.game.scheduler;

import io.github.pulsebeat02.murderrun.game.Game;
import java.util.function.Supplier;

public final class ConditionalTask extends GameScheduledTask {

  private final Supplier<Boolean> condition;

  public ConditionalTask(
      final Game game, final Runnable runnable, final Supplier<Boolean> condition) {
    super(game, runnable);
    this.condition = condition;
  }

  @Override
  public void run() {
    super.run();
    if (this.condition.get()) {
      this.cancel();
    }
  }
}
