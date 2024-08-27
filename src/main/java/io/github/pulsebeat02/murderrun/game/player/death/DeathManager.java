package io.github.pulsebeat02.murderrun.game.player.death;

import java.util.Collection;
import java.util.HashSet;
import java.util.Iterator;
import org.bukkit.entity.ArmorStand;
import org.checkerframework.checker.nullness.qual.Nullable;

public final class DeathManager {

  private @Nullable ArmorStand corpse;
  private final Collection<PlayerDeathTask> tasks;

  public DeathManager() {
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

  public @Nullable ArmorStand getCorpse() {
    return this.corpse;
  }

  public void setCorpse(final @Nullable ArmorStand corpse) {
    this.corpse = corpse;
  }
}
