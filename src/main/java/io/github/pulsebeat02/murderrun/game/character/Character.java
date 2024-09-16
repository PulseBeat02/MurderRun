package io.github.pulsebeat02.murderrun.game.character;

import io.github.pulsebeat02.murderrun.game.Game;
import io.github.pulsebeat02.murderrun.game.character.ability.AbstractAbility;
import io.github.pulsebeat02.murderrun.game.player.GamePlayer;
import java.util.Collection;

public interface Character {

  void scheduleTask();

  void preparePlayer(final GamePlayer player);

  Game getGame();

  GamePlayer getPlayer();

  Collection<AbstractAbility> getAbilities();
}
