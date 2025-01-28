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
import io.github.pulsebeat02.murderrun.game.scheduler.reference.SchedulerReference;
import java.util.HashSet;
import java.util.Set;
import java.util.concurrent.atomic.AtomicBoolean;
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

  public BukkitTask scheduleConditionalTask(
    final Runnable runnable,
    final long delay,
    final long period,
    final BooleanSupplier condition,
    final SchedulerReference reference
  ) {
    final ConditionalTask task = new ConditionalTask(this.game, runnable, condition, reference);
    final BukkitTask bukkit = task.runTaskTimer(this.plugin, delay, period);
    this.tasks.add(bukkit);
    return bukkit;
  }

  public BukkitTask scheduleTask(final Runnable runnable, final long delay, final SchedulerReference reference) {
    final GameScheduledTask task = new GameScheduledTask(this.game, runnable, reference);
    final BukkitTask bukkit = task.runTaskLater(this.plugin, delay);
    this.tasks.add(bukkit);
    return bukkit;
  }

  public BukkitTask scheduleRepeatedTask(final Runnable runnable, final long delay, final long period, final SchedulerReference reference) {
    final GameScheduledTask task = new GameScheduledTask(this.game, runnable, reference);
    final BukkitTask bukkit = task.runTaskTimer(this.plugin, delay, period);
    this.tasks.add(bukkit);
    return bukkit;
  }

  public BukkitTask scheduleRepeatedTask(
    final Runnable runnable,
    final long delay,
    final long period,
    final long duration,
    final SchedulerReference reference
  ) {
    final TemporaryRepeatedTask custom = new TemporaryRepeatedTask(this.game, runnable, period, duration, reference);
    final BukkitTask bukkit = custom.runTaskTimer(this.plugin, delay, period);
    this.tasks.add(bukkit);
    return bukkit;
  }

  public BukkitTask scheduleCountdownTask(final Consumer<Integer> tasks, final int seconds, final SchedulerReference reference) {
    final CountdownTask task = new CountdownTask(this.game, () -> {}, seconds, tasks, reference);
    final BukkitTask bukkit = task.runTaskTimer(this.plugin, 0, 20);
    this.tasks.add(bukkit);
    return bukkit;
  }

  public BukkitTask scheduleTaskAfterOnGround(final Runnable runnable, final Entity item) {
    final AtomicBoolean onFloor = new AtomicBoolean(false);
    final SchedulerReference reference = EntityReference.of(item);
    final Runnable internal = () -> this.waitForFall0(runnable, item, onFloor);
    final BukkitTask task = this.scheduleConditionalTask(internal, 0, 20L, onFloor::get, reference);
    this.tasks.add(task);
    return task;
  }

  private void waitForFall0(final Runnable runnable, final Entity item, final AtomicBoolean onFloor) {
    if (item.isOnGround()) {
      onFloor.set(true);
      runnable.run();
    }
  }

  public BukkitTask scheduleTaskUntilDeath(final Runnable runnable, final Entity entity) {
    final AtomicBoolean dead = new AtomicBoolean(false);
    final SchedulerReference reference = EntityReference.of(entity);
    final Runnable internal = () -> this.waitForDeathRepeat(runnable, entity, dead);
    final BukkitTask task = this.scheduleConditionalTask(internal, 0, 5L, dead::get, reference);
    this.tasks.add(task);
    return task;
  }

  public BukkitTask scheduleParticleTaskUntilDeath(final Item item, final Color color) {
    final World world = item.getWorld();
    final Runnable particleTask = () -> this.spawnParticles0(item, color, world);
    final Runnable afterOnGround = () -> this.scheduleTaskAfterOnGround(particleTask, item);
    return this.scheduleTaskUntilDeath(afterOnGround, item);
  }

  private void spawnParticles0(final Item item, final Color color, final World world) {
    final Location location = item.getLocation();
    world.spawnParticle(Particle.DUST, location, 5, 0.5, 0.5, 0.5, new DustOptions(color, 2));
  }

  public BukkitTask scheduleAfterDeath(final Runnable runnable, final Entity item) {
    final AtomicBoolean dead = new AtomicBoolean(false);
    final SchedulerReference reference = EntityReference.of(item);
    final Runnable internal = () -> this.waitAfterDeath(runnable, item, dead);
    final BukkitTask task = this.scheduleConditionalTask(internal, 0, 20L, dead::get, reference);
    this.tasks.add(task);
    return task;
  }

  private void waitAfterDeath(final Runnable runnable, final Entity entity, final AtomicBoolean dead) {
    if (entity.isDead()) {
      dead.set(true);
      runnable.run();
    }
  }

  private void waitForDeathRepeat(final Runnable runnable, final Entity entity, final AtomicBoolean dead) {
    if (entity.isDead()) {
      dead.set(true);
    }
    runnable.run();
  }
}
