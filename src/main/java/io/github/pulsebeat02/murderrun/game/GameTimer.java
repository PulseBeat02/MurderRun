package io.github.pulsebeat02.murderrun.game;

public final class GameTimer {

  private long startTime;
  private long endTime;
  private long elapsedTime;
  private long totalTime;

  public void startTimer() {
    final int seconds = GameProperties.GAME_TIME_LIMIT;
    this.startTime = System.currentTimeMillis();
    this.totalTime = seconds * 1000L;
  }

  public void stopTimer() {
    this.endTime = System.currentTimeMillis();
    if (this.startTime == 0) {
      this.elapsedTime = 0;
    } else {
      this.elapsedTime = (this.endTime - this.startTime) / 1000L;
    }
  }

  public void invalidateElapsedTime() {
    this.elapsedTime = Integer.MAX_VALUE;
  }

  public long getTotalTime() {
    return this.totalTime;
  }

  public long getTimeLeft() {
    final long current = System.currentTimeMillis();
    return this.totalTime - (current - this.startTime);
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
