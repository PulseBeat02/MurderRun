package io.github.pulsebeat02.murderrun.game.scheduler;

import io.github.pulsebeat02.murderrun.game.Game;
import java.util.function.BooleanSupplier;

public final class ConditionalTask extends GameScheduledTask {

  private final BooleanSupplier condition;

  public ConditionalTask(final Game game, final Runnable runnable, final BooleanSupplier condition) {
    super(game, runnable);
    this.condition = condition;
  }

  @Override
  public void run() {
    super.run();
    if (this.condition.getAsBoolean()) {
      this.cancel();
    }
  }
}
