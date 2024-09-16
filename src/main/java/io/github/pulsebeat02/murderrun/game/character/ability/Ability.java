package io.github.pulsebeat02.murderrun.game.character.ability;

import io.github.pulsebeat02.murderrun.game.Game;
import io.github.pulsebeat02.murderrun.game.player.GamePlayer;
import org.checkerframework.checker.initialization.qual.UnderInitialization;

public interface Ability {

  boolean checkActivation(@UnderInitialization Ability this);

  void applyAbility(@UnderInitialization Ability this, final Game game, final GamePlayer player);

  Runnable getTask();
}
