package io.github.pulsebeat02.murderrun.game;

import io.github.pulsebeat02.murderrun.utils.ExecutorUtils;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public final class GameExecutor {

  private final ExecutorService virtual;

  public GameExecutor() {
    this.virtual = Executors.newVirtualThreadPerTaskExecutor();
  }

  public ExecutorService getVirtualExecutor() {
    return this.virtual;
  }

  public void shutdown() {
    ExecutorUtils.shutdownExecutorGracefully(this.virtual);
  }
}
