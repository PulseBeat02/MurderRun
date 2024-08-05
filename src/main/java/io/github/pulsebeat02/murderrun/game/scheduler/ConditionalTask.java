package io.github.pulsebeat02.murderrun.game.scheduler;

import io.github.pulsebeat02.murderrun.game.MurderGame;
import java.util.function.Supplier;

public final class ConditionalTask extends MurderScheduler {

  private final Supplier<Boolean> condition;

  public ConditionalTask(
      final MurderGame game, final Runnable runnable, final Supplier<Boolean> condition) {
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
