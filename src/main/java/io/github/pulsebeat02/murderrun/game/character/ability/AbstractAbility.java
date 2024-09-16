package io.github.pulsebeat02.murderrun.game.character.ability;

import io.github.pulsebeat02.murderrun.game.Game;
import io.github.pulsebeat02.murderrun.game.player.GamePlayer;
import org.checkerframework.checker.initialization.qual.UnderInitialization;

public abstract class AbstractAbility implements Ability {

  private final Runnable task;

  public AbstractAbility(final Game game, final GamePlayer player) {
    this.task = () -> this.handleAbility(game, player);
  }

  private void handleAbility(
      @UnderInitialization AbstractAbility this, final Game game, final GamePlayer player) {
    if (this.checkActivation()) {
      this.applyAbility(game, player);
    }
  }

  @Override
  public Runnable getTask() {
    return this.task;
  }
}
