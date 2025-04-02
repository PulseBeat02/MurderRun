/*
 * This file is part of Murder Run, a spin-off game-mode of Dead by Daylight
 * Copyright (C) Brandon Li <https://brandonli.me/>
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program.  If not, see <https://www.gnu.org/licenses/>.
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
