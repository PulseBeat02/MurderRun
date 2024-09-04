package io.github.pulsebeat02.murderrun.game;

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
