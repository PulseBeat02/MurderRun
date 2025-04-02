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
package io.github.pulsebeat02.murderrun.game.player.death;

import io.github.pulsebeat02.murderrun.game.Game;
import io.github.pulsebeat02.murderrun.game.player.GamePlayer;
import io.github.pulsebeat02.murderrun.game.scheduler.GameScheduler;
import io.github.pulsebeat02.murderrun.game.scheduler.reference.LoosePlayerReference;
import java.util.Collection;
import java.util.HashSet;
import java.util.Iterator;
import net.citizensnpcs.api.npc.NPC;
import org.checkerframework.checker.nullness.qual.Nullable;

public final class DeathManager {

  private @Nullable NPC corpse;
  private final Collection<PlayerDeathTask> tasks;
  private final GamePlayer player;

  public DeathManager(final GamePlayer player) {
    this.tasks = new HashSet<>();
    this.player = player;
  }

  public boolean checkDeathCancellation() {
    boolean cancel;
    final Iterator<PlayerDeathTask> iterator = this.tasks.iterator();
    while (iterator.hasNext()) {
      final PlayerDeathTask task = iterator.next();
      cancel = task.isCancelDeath();
      if (cancel) {
        final Game game = this.player.getGame();
        final GameScheduler scheduler = game.getScheduler();
        final LoosePlayerReference playerReference = LoosePlayerReference.of(this.player);
        scheduler.scheduleTask(task, 2 * 20L, playerReference);
        task.run();
        iterator.remove();
        return true;
      }
    }
    return false;
  }

  public void runDeathTasks() {
    final Game game = this.player.getGame();
    final GameScheduler scheduler = game.getScheduler();
    final LoosePlayerReference playerReference = LoosePlayerReference.of(this.player);
    scheduler.scheduleTask(
      () -> {
        final Iterator<PlayerDeathTask> iterator = this.tasks.iterator();
        while (iterator.hasNext()) {
          final PlayerDeathTask task = iterator.next();
          task.run();
          iterator.remove();
        }
      },
      2 * 20L,
      playerReference
    );
  }

  public void addDeathTask(final PlayerDeathTask task) {
    this.tasks.add(task);
  }

  public void removeDeathTask(final PlayerDeathTask task) {
    this.tasks.remove(task);
  }

  public Collection<PlayerDeathTask> getDeathTasks() {
    return this.tasks;
  }

  public @Nullable NPC getCorpse() {
    return this.corpse;
  }

  public void setCorpse(final @Nullable NPC corpse) {
    this.corpse = corpse;
  }
}
