package io.github.pulsebeat02.murderrun.game;

public final class GameTimer {

  private long startTime;
  private long endTime;
  private long elapsedTime;

  public GameTimer() {}

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

  public long getStartTime() {
    return this.startTime;
  }

  public void setStartTime(final long startTime) {
    this.startTime = startTime;
  }

  public long getEndTime() {
    return this.endTime;
  }

  public void setEndTime(final long endTime) {
    this.endTime = endTime;
  }

  public long getElapsedTime() {
    return this.elapsedTime;
  }

  public void setElapsedTime(final long elapsedTime) {
    this.elapsedTime = elapsedTime;
  }
}
