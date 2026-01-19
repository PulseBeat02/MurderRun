/*
 * This file is part of Murder Run, a spin-off game-mode of Dead by Daylight
 * Copyright (C) Brandon Li <https://brandonli.me/>
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program.  If not, see <https://www.gnu.org/licenses/>.
 */
package me.brandonli.murderrun.utils;

import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.TimeUnit;

public final class ExecutorUtils {

  private ExecutorUtils() {
    throw new UnsupportedOperationException("Utility class cannot be instantiated");
  }

  public static boolean shutdownExecutorGracefully(final ExecutorService service) {
    service.shutdown();
    try {
      final boolean await = service.awaitTermination(5, TimeUnit.SECONDS);
      if (!await) {
        final List<Runnable> tasks = service.shutdownNow();
        if (!tasks.isEmpty()) {
          final String msg = createExecutorShutdownErrorMessage(tasks);
          throw new AssertionError(msg);
        }
      }
      return true;
    } catch (final InterruptedException e) {
      final Thread current = Thread.currentThread();
      current.interrupt(); // yeah... we're fucked
      throw new AssertionError(e);
    }
  }

  private static String createExecutorShutdownErrorMessage(final List<Runnable> tasks) {
    final int count = tasks.size();
    return "%s tasks uncompleted!".formatted(count);
  }
}
