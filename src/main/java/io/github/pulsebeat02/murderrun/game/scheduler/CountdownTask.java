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

import io.github.pulsebeat02.murderrun.game.Game;
import io.github.pulsebeat02.murderrun.game.scheduler.reference.Reference;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.function.Consumer;

public final class CountdownTask extends GameScheduledTask {

  private final AtomicInteger seconds;
  private final Consumer<Integer> tasks;

  public CountdownTask(
    final Game game,
    final Runnable runnable,
    final int seconds,
    final Consumer<Integer> tasks,
    final Reference<?> reference
  ) {
    super(game, runnable, reference);
    this.seconds = new AtomicInteger(seconds + 1);
    this.tasks = tasks;
  }

  @Override
  public void run() {
    super.run();
    final int seconds = this.seconds.decrementAndGet();
    this.tasks.accept(seconds);
    if (seconds <= 0) {
      this.cancel();
    }
  }
}
