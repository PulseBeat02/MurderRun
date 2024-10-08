package io.github.pulsebeat02.murderrun.game.character;

import io.github.pulsebeat02.murderrun.game.Game;
import io.github.pulsebeat02.murderrun.game.character.ability.AbstractAbility;
import io.github.pulsebeat02.murderrun.game.player.GamePlayer;
import io.github.pulsebeat02.murderrun.game.scheduler.GameScheduler;
import java.util.Collection;

public abstract class AbstractCharacter implements Character {

  private final Game game;
  private final GamePlayer player;
  private final Collection<AbstractAbility> abilities;

  public AbstractCharacter(final Game game, final GamePlayer player, final Collection<AbstractAbility> abilities) {
    this.game = game;
    this.player = player;
    this.abilities = abilities;
  }

  @Override
  public void scheduleTask() {
    final GameScheduler scheduler = this.game.getScheduler();
    scheduler.scheduleRepeatedTask(
      () -> {
        for (final AbstractAbility ability : this.abilities) {
          final Runnable task = ability.getTask();
          task.run();
        }
      },
      0L,
      5L
    );
    this.preparePlayer(this.player);
  }

  @Override
  public Game getGame() {
    return this.game;
  }

  @Override
  public GamePlayer getPlayer() {
    return this.player;
  }

  @Override
  public Collection<AbstractAbility> getAbilities() {
    return this.abilities;
  }
}
