/*

MIT License

Copyright (c) 2024 Brandon Li

Permission is hereby granted, free of charge, to any person obtaining a copy
of this software and associated documentation files (the "Software"), to deal
in the Software without restriction, including without limitation the rights
to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
copies of the Software, and to permit persons to whom the Software is
furnished to do so, subject to the following conditions:

The above copyright notice and this permission notice shall be included in all
copies or substantial portions of the Software.

THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE
SOFTWARE.

*/
package io.github.pulsebeat02.murderrun.game.character;

import io.github.pulsebeat02.murderrun.game.Game;
import io.github.pulsebeat02.murderrun.game.character.ability.AbstractAbility;
import io.github.pulsebeat02.murderrun.game.player.GamePlayer;
import io.github.pulsebeat02.murderrun.game.scheduler.GameScheduler;
import io.github.pulsebeat02.murderrun.game.scheduler.reference.PlayerReference;
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
    final PlayerReference reference = PlayerReference.of(this.player);
    scheduler.scheduleRepeatedTask(
      () -> {
        for (final AbstractAbility ability : this.abilities) {
          final Runnable task = ability.getTask();
          task.run();
        }
      },
      0L,
      5L,
      reference
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
