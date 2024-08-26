package io.github.pulsebeat02.murderrun.game.player.death;

import io.github.pulsebeat02.murderrun.game.player.GamePlayer;
import java.util.Collection;
import java.util.HashSet;
import java.util.Iterator;

public final class DeathManager {

  private final GamePlayer gamePlayer;
  private final Collection<PlayerDeathTask> tasks;

  public DeathManager(final GamePlayer gamePlayer) {
    this.gamePlayer = gamePlayer;
    this.tasks = new HashSet<>();
  }

  public boolean checkDeathCancellation() {
    boolean cancel;
    final Iterator<PlayerDeathTask> iterator = this.tasks.iterator();
    while (iterator.hasNext()) {
      final PlayerDeathTask task = iterator.next();
      cancel = task.isCancelDeath();
      if (cancel) {
        task.run();
        iterator.remove();
        return true;
      }
    }
    return false;
  }

  public void runDeathTasks() {
    final Iterator<PlayerDeathTask> iterator = this.tasks.iterator();
    while (iterator.hasNext()) {
      final PlayerDeathTask task = iterator.next();
      task.run();
      iterator.remove();
    }
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
}
