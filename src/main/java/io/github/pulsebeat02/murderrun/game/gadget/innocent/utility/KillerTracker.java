package io.github.pulsebeat02.murderrun.game.gadget.innocent.utility;

import io.github.pulsebeat02.murderrun.game.MurderGame;
import io.github.pulsebeat02.murderrun.game.gadget.MurderGadget;
import io.github.pulsebeat02.murderrun.game.player.GamePlayer;
import io.github.pulsebeat02.murderrun.game.player.MurderPlayerManager;
import io.github.pulsebeat02.murderrun.game.player.Murderer;
import io.github.pulsebeat02.murderrun.locale.Locale;
import io.github.pulsebeat02.murderrun.utils.NamespacedKeys;
import java.util.Collection;
import net.kyori.adventure.text.Component;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.PlayerInventory;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.persistence.PersistentDataContainer;
import org.bukkit.persistence.PersistentDataType;

public final class KillerTracker extends MurderGadget {

  public KillerTracker() {
    super(
        "killer_tracker",
        Material.COMPASS,
        Locale.KILLER_TRACKER_TRAP_NAME.build(),
        Locale.KILLER_TRACKER_TRAP_LORE.build(),
        stack -> {
          final ItemMeta meta = stack.getItemMeta();
          if (meta == null) {
            throw new AssertionError("Failed to create killer tracker!");
          }
          final PersistentDataContainer container = meta.getPersistentDataContainer();
          container.set(NamespacedKeys.KILLER_TRACKER, PersistentDataType.INTEGER, 0);
        });
  }

  public void onRightClickEvent(final MurderGame game, final PlayerInteractEvent event) {

    final MurderPlayerManager manager = game.getPlayerManager();
    final Player player = event.getPlayer();
    final GamePlayer gamePlayer = manager.lookupPlayer(player).orElseThrow();
    final Location location = player.getLocation();
    final double distance = this.getNearestKillerDistance(manager, location);
    final int count = this.increaseAndGetKillerCount(player);
    final boolean destroy = count == 5;
    super.onGadgetRightClick(game, event, destroy);

    final Component message = Locale.KILLER_TRACKER_ACTIVATE.build(distance);
    gamePlayer.sendMessage(message);
  }

  private int increaseAndGetKillerCount(final Player player) {

    final PlayerInventory inventory = player.getInventory();
    final ItemStack stack = inventory.getItemInMainHand();
    final ItemMeta meta = stack.getItemMeta();
    if (meta == null) {
      throw new AssertionError("Failed to retrieve killer tracker meta!");
    }

    final PersistentDataContainer container = meta.getPersistentDataContainer();
    final Integer val = container.get(NamespacedKeys.KILLER_TRACKER, PersistentDataType.INTEGER);
    if (val == null) {
      throw new AssertionError("Failed to retrieve killer tracker value!");
    }

    final int count = val + 1;
    container.set(NamespacedKeys.KILLER_TRACKER, PersistentDataType.INTEGER, count);

    return count;
  }

  private double getNearestKillerDistance(
      final MurderPlayerManager manager, final Location origin) {
    double min = Double.MAX_VALUE;
    final Collection<Murderer> killers = manager.getMurderers();
    for (final GamePlayer killer : killers) {
      final Location location = killer.getLocation();
      final double distance = location.distanceSquared(origin);
      if (distance < min) {
        min = distance;
      }
    }
    return min;
  }
}
