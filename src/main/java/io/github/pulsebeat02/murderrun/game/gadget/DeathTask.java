package io.github.pulsebeat02.murderrun.game.gadget;

import org.bukkit.scheduler.BukkitRunnable;

public final class DeathTask extends BukkitRunnable {

  private final Runnable task;
  private final boolean cancelDeath;

  public DeathTask(final Runnable task, final boolean cancelDeath) {
    this.task = task;
    this.cancelDeath = cancelDeath;
  }

  public Runnable getTask() {
    return task;
  }

  public boolean isCancelDeath() {
    return cancelDeath;
  }

  @Override
  public void run() {
    task.run();
  }
}
