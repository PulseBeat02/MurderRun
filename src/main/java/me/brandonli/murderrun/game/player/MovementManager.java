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
package me.brandonli.murderrun.game.player;

import java.util.AbstractMap.SimpleEntry;
import java.util.Iterator;
import java.util.Map;
import java.util.Map.Entry;
import java.util.concurrent.ConcurrentHashMap;
import me.brandonli.murderrun.game.Game;
import me.brandonli.murderrun.game.GameStatus;
import me.brandonli.murderrun.game.scheduler.GameScheduler;
import me.brandonli.murderrun.game.scheduler.reference.NullReference;
import me.brandonli.murderrun.utils.structure.CircularBuffer;
import org.bukkit.Location;
import org.checkerframework.checker.nullness.qual.Nullable;

public final class MovementManager {

  private static final int BUFFER_SIZE = 100 * (5 * 20);

  private final Game game;
  private final Map<GamePlayer, CircularBuffer<Entry<Location, Long>>> playerLocations;

  public MovementManager(final Game game) {
    this.game = game;
    this.playerLocations = new ConcurrentHashMap<>();
  }

  public void start() {
    final GameScheduler scheduler = this.game.getScheduler();
    final GamePlayerManager manager = this.game.getPlayerManager();
    final NullReference reference = NullReference.of();
    scheduler.scheduleRepeatedTask(() -> this.trackMovement(manager), 0, 5, reference);
  }

  private void trackMovement(final GamePlayerManager manager) {
    final GameStatus status = this.game.getStatus();
    final GameStatus.Status gameStatus = status.getStatus();
    if (gameStatus != GameStatus.Status.KILLERS_RELEASED) {
      return;
    }

    manager.applyToAllParticipants(player -> {
      final Location location = player.getLocation();
      final long timestamp = System.currentTimeMillis();
      final CircularBuffer<Entry<Location, Long>> entries =
          this.playerLocations.computeIfAbsent(player, _ -> new CircularBuffer<>(BUFFER_SIZE));
      final SimpleEntry<Location, Long> entry = new SimpleEntry<>(location, timestamp);
      entries.add(entry);
    });
  }

  public @Nullable CircularBuffer<Entry<Location, Long>> getBuffer(final GamePlayer player) {
    return this.playerLocations.get(player);
  }

  private boolean checkReady(
      final CircularBuffer<Entry<Location, Long>> buffer, final long current) {
    final Entry<Location, Long> oldest = buffer.getOldest();
    final long value = oldest.getValue();
    final long difference = current - value;
    return difference > 5000;
  }

  public boolean handleRewind(final GamePlayer player) {
    final CircularBuffer<Entry<Location, Long>> buffer = this.getBuffer(player);
    if (buffer == null) {
      return false;
    }

    final long currentTime = System.currentTimeMillis();
    if (!this.checkReady(buffer, currentTime)) {
      return false;
    }

    final Iterator<Entry<Location, Long>> iterator = buffer.iterator();
    while (iterator.hasNext()) {
      final Entry<Location, Long> entry = iterator.next();
      final long value = entry.getValue();
      final long difference = currentTime - value;
      if (difference <= 5000) {
        final Location key = entry.getKey();
        player.teleport(key);
        iterator.remove();
        break;
      }
      iterator.remove();
    }

    return true;
  }

  public void cleanup() {
    this.playerLocations.clear();
  }
}
