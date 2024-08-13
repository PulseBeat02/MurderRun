package io.github.pulsebeat02.murderrun.utils;

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
        final String msg = createExecutorShutdownErrorMessage(tasks);
        System.err.println(msg);
        return false;
      }
    } catch (final InterruptedException e) {
      throw new AssertionError(e);
    }
    return false;
  }

  private static String createExecutorShutdownErrorMessage(final List<Runnable> tasks) {
    final int count = tasks.size();
    return "%s tasks uncompleted!".formatted(count);
  }
}
