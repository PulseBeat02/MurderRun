package io.github.pulsebeat02.murderrun.utils;

import java.util.concurrent.atomic.AtomicLong;
import org.bukkit.Bukkit;
import org.bukkit.plugin.Plugin;
import org.bukkit.plugin.PluginManager;
import org.bukkit.scheduler.BukkitRunnable;
import org.bukkit.scheduler.BukkitScheduler;

public final class SchedulingUtils {

  private static final Plugin PLUGIN;

  static {
    final PluginManager manager = Bukkit.getPluginManager();
    final Plugin plugin = manager.getPlugin("MurderRun");
    if (plugin == null) {
      throw new AssertionError("Failed to retrieve plugin class!");
    }
    PLUGIN = plugin;
  }

  private SchedulingUtils() {
    throw new UnsupportedOperationException("Utility class cannot be instantiated");
  }

  public static void scheduleTask(final Runnable runnable, final long delay) {
    final BukkitScheduler scheduler = Bukkit.getScheduler();
    scheduler.runTaskLater(PLUGIN, runnable, delay);
  }

  public static void scheduleRepeatingTaskDuration(
      final Runnable runnable, final long delay, final long period, final long duration) {

    final long count = (duration + period - 1) / period; // ensures that we round up by 1
    final class CustomRunnable extends BukkitRunnable {

      final AtomicLong time = new AtomicLong(count);

      @Override
      public void run() {
        runnable.run();
        final long raw = this.time.decrementAndGet();
        if (raw <= 0) {
          this.cancel();
        }
      }
    }

    final CustomRunnable custom = new CustomRunnable();
    custom.runTaskTimer(PLUGIN, delay, period);
  }
}
