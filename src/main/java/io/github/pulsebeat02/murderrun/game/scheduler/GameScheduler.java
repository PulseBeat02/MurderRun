package io.github.pulsebeat02.murderrun.game.scheduler;

import io.github.pulsebeat02.murderrun.MurderRun;
import io.github.pulsebeat02.murderrun.game.Game;
import java.util.HashSet;
import java.util.Set;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.function.Consumer;
import java.util.function.Supplier;
import org.bukkit.Color;
import org.bukkit.Location;
import org.bukkit.Particle;
import org.bukkit.Particle.DustOptions;
import org.bukkit.World;
import org.bukkit.entity.Item;
import org.bukkit.scheduler.BukkitTask;

public final class GameScheduler {

  private final MurderRun plugin;
  private final Game game;
  private final Set<BukkitTask> tasks;

  public GameScheduler(final Game game) {
    this.game = game;
    this.plugin = game.getPlugin();
    this.tasks = new HashSet<>();
  }

  public void cancelAllTasks() {
    this.tasks.forEach(BukkitTask::cancel);
    this.tasks.clear();
  }

  public BukkitTask scheduleConditionalTask(
      final Runnable runnable,
      final long delay,
      final long period,
      final Supplier<Boolean> condition) {
    final ConditionalTask task = new ConditionalTask(this.game, runnable, condition);
    final BukkitTask bukkit = task.runTaskTimer(this.plugin, delay, period);
    this.tasks.add(bukkit);
    return bukkit;
  }

  public BukkitTask scheduleTask(final Runnable runnable, final long delay) {
    final GameScheduledTask task = new GameScheduledTask(this.game, runnable);
    final BukkitTask bukkit = task.runTaskLater(this.plugin, delay);
    this.tasks.add(bukkit);
    return bukkit;
  }

  public BukkitTask scheduleRepeatedTask(
      final Runnable runnable, final long delay, final long period) {
    final GameScheduledTask task = new GameScheduledTask(this.game, runnable);
    final BukkitTask bukkit = task.runTaskTimer(this.plugin, delay, period);
    this.tasks.add(bukkit);
    return bukkit;
  }

  public BukkitTask scheduleRepeatedTask(
      final Runnable runnable, final long delay, final long period, final long duration) {
    final TemporaryRepeatedTask custom =
        new TemporaryRepeatedTask(this.game, runnable, period, duration);
    final BukkitTask bukkit = custom.runTaskTimer(this.plugin, delay, period);
    this.tasks.add(bukkit);
    return bukkit;
  }

  public BukkitTask scheduleCountdownTask(final Consumer<Integer> tasks, final int seconds) {
    final CountdownTask task = new CountdownTask(this.game, () -> {}, seconds, tasks);
    final BukkitTask bukkit = task.runTaskTimer(this.plugin, 0, 20);
    this.tasks.add(bukkit);
    return bukkit;
  }

  public BukkitTask scheduleTaskWhenItemFalls(final Runnable runnable, final Item item) {

    final AtomicBoolean onFloor = new AtomicBoolean(false);
    final Runnable internal = () -> {
      if (item.isOnGround()) {
        onFloor.set(true);
        runnable.run();
      }
    };

    final BukkitTask task = this.scheduleConditionalTask(internal, 0, 20L, onFloor::get);
    this.tasks.add(task);

    return task;
  }

  public BukkitTask scheduleParticleTask(final Item item, final Color color) {

    final World world = item.getWorld();
    final Runnable particleTask = () -> {
      final Location location = item.getLocation();
      world.spawnParticle(Particle.DUST, location, 1, 0.5, 0.5, 0.5, new DustOptions(color, 3));
    };

    final Runnable conditionalTask =
        () -> this.scheduleConditionalTask(particleTask, 0L, 5L, item::isDead);

    return this.scheduleTaskWhenItemFalls(conditionalTask, item);
  }
}
