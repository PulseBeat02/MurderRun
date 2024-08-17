package io.github.pulsebeat02.murderrun.game.gadget.survivor.utility;

import io.github.pulsebeat02.murderrun.game.Game;
import io.github.pulsebeat02.murderrun.game.gadget.survivor.SurvivorGadget;
import io.github.pulsebeat02.murderrun.game.player.GamePlayer;
import io.github.pulsebeat02.murderrun.game.player.PlayerManager;
import io.github.pulsebeat02.murderrun.game.player.Survivor;
import io.github.pulsebeat02.murderrun.locale.Message;
import io.github.pulsebeat02.murderrun.structure.CircularBuffer;
import java.util.AbstractMap.SimpleEntry;
import java.util.Map;
import java.util.WeakHashMap;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerDropItemEvent;
import org.bukkit.event.player.PlayerMoveEvent;

public final class Rewind extends SurvivorGadget implements Listener {

  private static final int BUFFER_SIZE = 5 * 5 * 20;
  private final Map<GamePlayer, CircularBuffer<SimpleEntry<Location, Long>>> playerLocations;
  private final Game game;

  public Rewind(final Game game) {
    super("rewind", Material.DIAMOND, Message.REWIND_NAME.build(), Message.REWIND_LORE.build(), 16);
    this.playerLocations = new WeakHashMap<>();
    this.game = game;
  }

  @EventHandler
  public void onPlayerMove(final PlayerMoveEvent event) {

    final Player player = event.getPlayer();
    final PlayerManager manager = game.getPlayerManager();
    if (!manager.checkPlayerExists(player)) {
      return;
    }

    final GamePlayer survivor = manager.getGamePlayer(player);
    if (!(survivor instanceof Survivor)) {
      return;
    }

    final Location location = player.getLocation();
    final long timestamp = System.currentTimeMillis();
    this.playerLocations.putIfAbsent(survivor, new CircularBuffer<>(BUFFER_SIZE));

    final CircularBuffer<SimpleEntry<Location, Long>> entries = this.playerLocations.get(survivor);
    final SimpleEntry<Location, Long> entry = new SimpleEntry<>(location, timestamp);
    entries.add(entry);
  }

  @Override
  public void onGadgetDrop(final Game game, final PlayerDropItemEvent event, final boolean remove) {

    super.onGadgetDrop(game, event, true);

    final Player player = event.getPlayer();
    final PlayerManager manager = game.getPlayerManager();
    final GamePlayer survivor = manager.getGamePlayer(player);
    this.handleRewind(survivor);
  }

  private void handleRewind(final GamePlayer player) {
    final CircularBuffer<SimpleEntry<Location, Long>> buffer = this.playerLocations.get(player);
    if (buffer != null && buffer.isFull()) {
      final long currentTime = System.currentTimeMillis();
      SimpleEntry<Location, Long> rewindEntry = buffer.getOldest();
      for (final SimpleEntry<Location, Long> entry : buffer) {
        if (currentTime - entry.getValue() >= 5000) {
          rewindEntry = entry;
          break;
        }
      }
      player.teleport(rewindEntry.getKey());
    }
  }
}
