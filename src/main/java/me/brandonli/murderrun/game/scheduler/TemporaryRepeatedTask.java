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
package me.brandonli.murderrun.game.scheduler;

import java.util.concurrent.atomic.AtomicLong;
import me.brandonli.murderrun.game.Game;
import me.brandonli.murderrun.game.scheduler.reference.Reference;

public final class TemporaryRepeatedTask extends GameScheduledTask {

  private final AtomicLong time;

  public TemporaryRepeatedTask(
    final Game game,
    final Runnable runnable,
    final long period,
    final long duration,
    final Reference<?> reference
  ) {
    super(game, runnable, reference);
    final long count = (duration + period - 1) / period;
    this.time = new AtomicLong(count);
  }

  @Override
  public void run() {
    super.run();
    final long raw = this.time.decrementAndGet();
    if (raw <= 0) {
      this.cancel();
    }
  }
}
