package io.github.pulsebeat02.murderrun.game;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;

public final class GameExecutors {

  private final ExecutorService virtualExecutor;
  private final ScheduledExecutorService scheduledExecutor;

  public GameExecutors() {
    this.virtualExecutor = Executors.newVirtualThreadPerTaskExecutor();
    this.scheduledExecutor = Executors.newScheduledThreadPool(8);
  }

  public void shutdown() {
    this.virtualExecutor.shutdown();
    this.scheduledExecutor.shutdown();
  }

  public ExecutorService getVirtualExecutor() {
    return this.virtualExecutor;
  }

  public ScheduledExecutorService getScheduledExecutor() {
    return this.scheduledExecutor;
  }
}
