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
import me.brandonli.murderrun.game.GameProperties;
import org.bukkit.scheduler.BukkitRunnable;

public final class PreGameExpirationTimer extends BukkitRunnable {

  private final PreGamePlayerManager manager;
  private final AtomicInteger seconds;

  public PreGameExpirationTimer(final PreGamePlayerManager manager) {
    this.manager = manager;
    this.seconds = new AtomicInteger(0);
  }

  @Override
  public void run() {
    final int current = this.seconds.incrementAndGet();
    final PreGameManager preGameManager = this.manager.getManager();
    final GameProperties properties = preGameManager.getProperties();
    final int shutdownSeconds = properties.getGameExpirationTime();
    if (current >= shutdownSeconds) {
      final GameManager gameManager = preGameManager.getGameManager();
      final String id = preGameManager.getId();
      gameManager.removeGame(id);
      this.cancel();
    }
  }
}
