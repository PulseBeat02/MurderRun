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
        throw new AssertionError(msg);
      }
    } catch (final InterruptedException e) {
      final Thread current = Thread.currentThread();
      current.interrupt(); // yeah... we're fucked
    }
    return false;
  }

  private static String createExecutorShutdownErrorMessage(final List<Runnable> tasks) {
    final int count = tasks.size();
    return "%s tasks uncompleted!".formatted(count);
  }
}
