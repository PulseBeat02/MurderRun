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
package io.github.pulsebeat02.murderrun.game.scheduler;

import io.github.pulsebeat02.murderrun.MurderRun;
import io.github.pulsebeat02.murderrun.game.Game;
import io.github.pulsebeat02.murderrun.game.scheduler.reference.EntityReference;
import io.github.pulsebeat02.murderrun.game.scheduler.reference.Reference;
import java.util.HashSet;
import java.util.Set;
import java.util.function.BooleanSupplier;
import java.util.function.Consumer;
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

  public void cancelTask(final BukkitTask task) {
    if (task != null) {
      task.cancel();
      this.tasks.remove(task);
    }
  }

  public void cancelTask(final int taskId) {
    for (final BukkitTask task : this.tasks) {
      if (task.getTaskId() == taskId) {
        task.cancel();
        this.tasks.remove(task);
        break;
      }
    }
  }

  public BukkitTask scheduleConditionalTask(
    final Runnable runnable,
    final long delay,
    final long period,
    final BooleanSupplier condition,
    final Reference<?> reference
  ) {
    final ConditionalTask task = new ConditionalTask(this.game, runnable, condition, reference);
    return this.scheduleTask(task, delay, period);
  }

  public BukkitTask scheduleTask(final Runnable runnable, final long delay, final Reference<?> reference) {
    final GameScheduledTask task = new GameScheduledTask(this.game, runnable, reference);
    return this.scheduleTask(task, delay, -1);
  }

  public BukkitTask scheduleRepeatedTask(final Runnable runnable, final long delay, final long period, final Reference<?> reference) {
    final GameScheduledTask task = new GameScheduledTask(this.game, runnable, reference);
    return this.scheduleTask(task, delay, period);
  }

  public BukkitTask scheduleRepeatedTask(
    final Runnable runnable,
    final long delay,
    final long period,
    final long duration,
    final Reference<?> reference
  ) {
    final TemporaryRepeatedTask task = new TemporaryRepeatedTask(this.game, runnable, period, duration, reference);
    return this.scheduleTask(task, delay, period);
  }

  public BukkitTask scheduleCountdownTask(final Consumer<Integer> tasks, final int seconds, final Reference<?> reference) {
    final Runnable dummy = () -> {};
    final CountdownTask countdownTask = new CountdownTask(this.game, dummy, seconds, tasks, reference);
    return this.scheduleTask(countdownTask, 0, 20);
  }

  public BukkitTask scheduleTaskUntilDeath(final Runnable runnable, final Entity entity) {
    final Reference<?> reference = EntityReference.of(entity);
    final Runnable task = () -> this.waitForDeath(runnable, entity);
    return this.scheduleConditionalTask(task, 0, 5L, entity::isDead, reference);
  }

  public BukkitTask scheduleParticleTaskUntilDeath(final Item item, final Color color) {
    final World world = item.getWorld();
    final Runnable particleTask = () -> this.spawnParticles(item, color, world);
    return this.scheduleTaskUntilDeath(particleTask, item);
  }

  public BukkitTask scheduleAfterDeath(final Runnable runnable, final Entity item) {
    final Reference<?> reference = EntityReference.of(item);
    final Runnable task = () -> this.waitAfterDeath(runnable, item);
    return this.scheduleConditionalTask(task, 0, 20L, item::isDead, reference);
  }

  private BukkitTask scheduleTask(final GameScheduledTask task, final long delay, final long period) {
    final BukkitTask bukkitTask = (period > 0) ? task.runTaskTimer(this.plugin, delay, period) : task.runTaskLater(this.plugin, delay);
    this.tasks.add(bukkitTask);
    return bukkitTask;
  }

  private void waitForFall(final Runnable runnable, final Entity item) {
    if (item.isOnGround()) {
      runnable.run();
    }
  }

  private void waitForDeath(final Runnable runnable, final Entity entity) {
    if (!entity.isDead()) {
      runnable.run();
    }
  }

  private void waitAfterDeath(final Runnable runnable, final Entity entity) {
    if (entity.isDead()) {
      runnable.run();
    }
  }

  private void spawnParticles(final Item item, final Color color, final World world) {
    final Location location = item.getLocation();
    world.spawnParticle(Particle.DUST, location, 5, 0.5, 0.5, 0.5, new DustOptions(color, 2));
  }
}
