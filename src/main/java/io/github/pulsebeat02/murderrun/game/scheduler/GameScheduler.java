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
import org.bukkit.entity.Entity;
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

  public BukkitTask scheduleTaskAfterOnGround(final Runnable runnable, final Entity item) {
    final AtomicBoolean onFloor = new AtomicBoolean(false);
    final Runnable internal = () -> this.waitForFall0(runnable, item, onFloor);
    final BukkitTask task = this.scheduleConditionalTask(internal, 0, 20L, onFloor::get);
    this.tasks.add(task);
    return task;
  }

  private void waitForFall0(
      final Runnable runnable, final Entity item, final AtomicBoolean onFloor) {
    if (item.isOnGround()) {
      onFloor.set(true);
      runnable.run();
    }
  }

  public BukkitTask scheduleTaskUntilDeath(final Runnable runnable, final Entity entity) {
    final AtomicBoolean dead = new AtomicBoolean(false);
    final Runnable internal = () -> this.waitForDeath(runnable, entity, dead);
    final BukkitTask task = this.scheduleConditionalTask(internal, 0, 10L, dead::get);
    this.tasks.add(task);
    return task;
  }

  public BukkitTask scheduleParticleTaskUntilDeath(final Item item, final Color color) {
    final World world = item.getWorld();
    final Runnable particleTask = () -> this.spawnParticles0(item, color, world);
    final Runnable conditionalTask = () -> this.scheduleTaskUntilDeath(particleTask, item);
    return this.scheduleTaskAfterOnGround(conditionalTask, item);
  }

  private void spawnParticles0(final Item item, final Color color, final World world) {
    final Location location = item.getLocation();
    world.spawnParticle(Particle.DUST, location, 10, 0.5, 0.5, 0.5, new DustOptions(color, 2));
  }

  public BukkitTask scheduleAfterDeath(final Runnable runnable, final Entity item) {
    final AtomicBoolean dead = new AtomicBoolean(false);
    final Runnable internal = () -> this.waitForDeath(runnable, item, dead);
    final BukkitTask task = this.scheduleConditionalTask(internal, 0, 20L, dead::get);
    this.tasks.add(task);
    return task;
  }

  private void waitForDeath(
      final Runnable runnable, final Entity entity, final AtomicBoolean dead) {
    if (entity.isDead()) {
      dead.set(true);
      runnable.run();
    }
  }
}
