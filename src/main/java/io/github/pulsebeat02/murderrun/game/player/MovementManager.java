package io.github.pulsebeat02.murderrun.game.player;

import static java.util.Objects.requireNonNull;

import io.github.pulsebeat02.murderrun.game.Game;
import io.github.pulsebeat02.murderrun.game.scheduler.GameScheduler;
import io.github.pulsebeat02.murderrun.structure.CircularBuffer;
import java.util.AbstractMap.SimpleEntry;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;
import org.bukkit.Location;

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
    final PlayerManager manager = this.game.getPlayerManager();
    this.addAllPlayers(manager);
    scheduler.scheduleRepeatedTask(() -> this.trackMovement(manager), 0, 1);
  }

  private void addAllPlayers(final PlayerManager manager) {
    manager.applyToAllParticipants(
        player -> this.playerLocations.put(player, new CircularBuffer<>(BUFFER_SIZE)));
  }

  private void trackMovement(final PlayerManager manager) {
    manager.applyToAllParticipants(player -> {
      final Location location = player.getLocation();
      final long timestamp = System.currentTimeMillis();
      final CircularBuffer<Entry<Location, Long>> entries =
          requireNonNull(this.playerLocations.get(player));
      final SimpleEntry<Location, Long> entry = new SimpleEntry<>(location, timestamp);
      entries.add(entry);
    });
  }

  public CircularBuffer<Entry<Location, Long>> getBuffer(final GamePlayer player) {
    return requireNonNull(this.playerLocations.get(player));
  }

  public boolean handleRewind(final GamePlayer player) {
    final CircularBuffer<Entry<Location, Long>> buffer = this.getBuffer(player);
    final long currentTime = System.currentTimeMillis();
    final Iterator<Entry<Location, Long>> iterator = buffer.iterator();
    final Set<Entry<Location, Long>> remove = new HashSet<>();
    while (iterator.hasNext()) {
      final Entry<Location, Long> entry = iterator.next();
      if (currentTime - entry.getValue() <= 5000) {
        final Location key = entry.getKey();
        player.teleport(key);
        buffer.removeAll(remove);
        return true;
      }
      remove.add(entry);
    }
    return false;
  }
}
