package io.github.pulsebeat02.murderrun.game.player.death;

import org.bukkit.scheduler.BukkitRunnable;

public final class PlayerDeathTask extends BukkitRunnable {

  private final Runnable task;
  private final boolean cancelDeath;

  public PlayerDeathTask(final Runnable task, final boolean cancelDeath) {
    this.task = task;
    this.cancelDeath = cancelDeath;
  }

  public Runnable getTask() {
    return this.task;
  }

  public boolean isCancelDeath() {
    return this.cancelDeath;
  }

  @Override
  public void run() {
    this.task.run();
  }
}
