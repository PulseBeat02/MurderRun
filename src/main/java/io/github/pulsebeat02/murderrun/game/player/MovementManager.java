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
package io.github.pulsebeat02.murderrun.game.player;

import io.github.pulsebeat02.murderrun.game.Game;
import io.github.pulsebeat02.murderrun.game.GameStatus;
import io.github.pulsebeat02.murderrun.game.scheduler.GameScheduler;
import io.github.pulsebeat02.murderrun.structure.CircularBuffer;
import java.util.AbstractMap.SimpleEntry;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.Map.Entry;
import org.bukkit.Location;
import org.checkerframework.checker.nullness.qual.Nullable;

public final class MovementManager {

  private static final int BUFFER_SIZE = 100 * (5 * 20);

  private final Game game;
  private final Map<GamePlayer, CircularBuffer<Entry<Location, Long>>> playerLocations;

  public MovementManager(final Game game) {
    this.game = game;
    this.playerLocations = new HashMap<>();
  }

  public void start() {
    final GameScheduler scheduler = this.game.getScheduler();
    final GamePlayerManager manager = this.game.getPlayerManager();
    scheduler.scheduleRepeatedTask(() -> this.trackMovement(manager), 0, 5);
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
      this.playerLocations.putIfAbsent(player, new CircularBuffer<>(BUFFER_SIZE));
      final CircularBuffer<Entry<Location, Long>> entries = this.playerLocations.get(player);
      final SimpleEntry<Location, Long> entry = new SimpleEntry<>(location, timestamp);
      entries.add(entry);
    });
  }

  public @Nullable CircularBuffer<Entry<Location, Long>> getBuffer(final GamePlayer player) {
    return this.playerLocations.get(player);
  }

  private boolean checkReady(final CircularBuffer<Entry<Location, Long>> buffer, final long current) {
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
}
