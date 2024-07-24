package io.github.pulsebeat02.murderrun.game;

public final class MurderTimeManager {

  private long startTime;
  private long endTime;
  private long elapsedTime;

  public MurderTimeManager() {}

  public void startTimer() {
    this.startTime = System.currentTimeMillis();
  }

  public void stopTimer() {
    this.endTime = System.currentTimeMillis();
    this.elapsedTime = this.endTime - this.startTime / 1000L;
  }

  public void invalidateElapsedTime() {
    this.elapsedTime = Integer.MAX_VALUE;
  }

  public long getElapsedTime() {
    return this.elapsedTime;
  }
}
