package io.github.pulsebeat02.murderrun.game.character.ability;

import io.github.pulsebeat02.murderrun.game.Game;
import io.github.pulsebeat02.murderrun.game.player.GamePlayer;

public interface Ability {

  boolean checkActivation();

  void applyAbility(final Game game, final GamePlayer player);

  Runnable getTask();
}
