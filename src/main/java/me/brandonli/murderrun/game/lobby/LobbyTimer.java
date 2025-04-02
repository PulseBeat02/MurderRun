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
package me.brandonli.murderrun.game.lobby;

import java.util.concurrent.atomic.AtomicInteger;
import java.util.function.Consumer;
import org.bukkit.scheduler.BukkitRunnable;

public final class LobbyTimer extends BukkitRunnable {

  private final AtomicInteger time;
  private final Consumer<Integer> timeConsumer;

  public LobbyTimer(final int time, final Consumer<Integer> timeConsumer) {
    this.time = new AtomicInteger(time);
    this.timeConsumer = timeConsumer;
  }

  @Override
  public void run() {
    final int time = this.time.get();
    this.timeConsumer.accept(time);
    if (time <= 0) {
      this.cancel();
    }
    this.time.decrementAndGet();
  }

  public void setTime(final int time) {
    this.time.set(time);
  }

  public int getTime() {
    return this.time.get();
  }
}
