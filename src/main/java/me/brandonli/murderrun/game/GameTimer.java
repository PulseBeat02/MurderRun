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
package me.brandonli.murderrun.game;

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
