package io.github.pulsebeat02.murderrun.game.scheduler;

import io.github.pulsebeat02.murderrun.MurderRun;
import io.github.pulsebeat02.murderrun.game.MurderGame;
import java.util.function.Supplier;
import org.bukkit.scheduler.BukkitTask;

public final class MurderGameScheduler {

  private final MurderRun plugin;
  private final MurderGame game;

  public MurderGameScheduler(final MurderGame game) {
    this.game = game;
    this.plugin = game.getPlugin();
  }

  public BukkitTask scheduleTaskUntilCondition(
      final Runnable runnable,
      final long delay,
      final long period,
      final Supplier<Boolean> condition) {
    final ConditionalTask task = new ConditionalTask(this.game, runnable, condition);
    return task.runTaskTimer(this.plugin, delay, period);
  }

  public BukkitTask scheduleTask(final Runnable runnable, final long delay) {
    final MurderScheduler task = new MurderScheduler(this.game, runnable);
    return task.runTaskLater(this.plugin, delay);
  }

  public BukkitTask scheduleRepeatedTask(
      final Runnable runnable, final long delay, final long period) {
    final MurderScheduler task = new MurderScheduler(this.game, runnable);
    return task.runTaskTimer(this.plugin, delay, period);
  }

  public BukkitTask scheduleRepeatedTask(
      final Runnable runnable, final long delay, final long period, final long duration) {
    final TemporaryRepeatedTask custom =
        new TemporaryRepeatedTask(this.game, runnable, period, duration);
    return custom.runTaskTimer(this.plugin, delay, period);
  }
}
