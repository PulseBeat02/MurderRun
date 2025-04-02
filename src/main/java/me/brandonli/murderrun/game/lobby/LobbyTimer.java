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
