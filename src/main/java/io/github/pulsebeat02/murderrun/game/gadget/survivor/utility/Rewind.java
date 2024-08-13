package io.github.pulsebeat02.murderrun.game.gadget.survivor.utility;

import io.github.pulsebeat02.murderrun.MurderRun;
import io.github.pulsebeat02.murderrun.game.Game;
import io.github.pulsebeat02.murderrun.game.gadget.survivor.SurvivorGadget;
import io.github.pulsebeat02.murderrun.locale.Locale;
import io.github.pulsebeat02.murderrun.utils.CircularBuffer;
import java.util.AbstractMap.SimpleEntry;
import java.util.Map;
import java.util.WeakHashMap;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerDropItemEvent;
import org.bukkit.event.player.PlayerMoveEvent;

public final class Rewind extends SurvivorGadget implements Listener {

  private static final int BUFFER_SIZE = 5 * 5 * 20;
  private final Map<Player, CircularBuffer<SimpleEntry<Location, Long>>> playerLocations;

  public Rewind(final MurderRun plugin) {
    super(
        "rewind",
        Material.DIAMOND,
        Locale.REWIND_TRAP_NAME.build(),
        Locale.REWIND_TRAP_LORE.build());
    this.playerLocations = new WeakHashMap<>();
    Bukkit.getPluginManager().registerEvents(this, plugin);
  }

  @EventHandler
  public void onPlayerMove(final PlayerMoveEvent event) {
    final Player player = event.getPlayer();
    final Location location = player.getLocation();
    final long timestamp = System.currentTimeMillis();
    this.playerLocations.putIfAbsent(player, new CircularBuffer<>(BUFFER_SIZE));
    this.playerLocations.get(player).add(new SimpleEntry<>(location, timestamp));
  }

  @Override
  public void onGadgetDrop(final Game game, final PlayerDropItemEvent event, final boolean remove) {
    super.onGadgetDrop(game, event, true);
    final Player player = event.getPlayer();
    this.handleRewind(player);
  }

  private void handleRewind(final Player player) {
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
