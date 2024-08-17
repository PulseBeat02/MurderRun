package io.github.pulsebeat02.murderrun.game.gadget.survivor.utility;

import static java.util.Objects.requireNonNull;

import io.github.pulsebeat02.murderrun.game.Game;
import io.github.pulsebeat02.murderrun.game.gadget.survivor.SurvivorGadget;
import io.github.pulsebeat02.murderrun.game.player.GamePlayer;
import io.github.pulsebeat02.murderrun.game.player.Killer;
import io.github.pulsebeat02.murderrun.game.player.PlayerManager;
import io.github.pulsebeat02.murderrun.immutable.Keys;
import io.github.pulsebeat02.murderrun.locale.Message;
import io.github.pulsebeat02.murderrun.utils.ItemUtils;
import java.util.Collection;
import net.kyori.adventure.text.Component;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.NamespacedKey;
import org.bukkit.entity.Player;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.PlayerInventory;
import org.bukkit.persistence.PersistentDataType;

public final class KillerTracker extends SurvivorGadget {

  public KillerTracker() {
    super(
        "killer_tracker",
        Material.COMPASS,
        Message.KILLER_TRACKER_NAME.build(),
        Message.KILLER_TRACKER_LORE.build(),
        32,
        stack -> ItemUtils.setPersistentDataAttribute(
            stack, Keys.KILLER_TRACKER, PersistentDataType.INTEGER, 0));
  }

  @Override
  public void onGadgetRightClick(
      final Game game, final PlayerInteractEvent event, final boolean remove) {

    final PlayerManager manager = game.getPlayerManager();
    final Player player = event.getPlayer();
    final GamePlayer gamePlayer = manager.getGamePlayer(player);
    final Location location = player.getLocation();
    final double distance = this.getNearestKillerDistance(manager, location);
    final int count = this.increaseAndGetKillerCount(player);
    final boolean destroy = count == 5;
    super.onGadgetRightClick(game, event, destroy);

    final Component message = Message.KILLER_TRACKER_ACTIVATE.build(distance);
    gamePlayer.sendMessage(message);
  }

  private int increaseAndGetKillerCount(final Player player) {

    final PlayerInventory inventory = player.getInventory();
    final ItemStack stack = inventory.getItemInMainHand();
    final NamespacedKey key = Keys.KILLER_TRACKER;
    final PersistentDataType<Integer, Integer> type = PersistentDataType.INTEGER;
    final Integer val = requireNonNull(ItemUtils.getPersistentDataAttribute(stack, key, type));
    final int count = val + 1;
    ItemUtils.setPersistentDataAttribute(stack, key, type, count);

    return count;
  }

  private double getNearestKillerDistance(final PlayerManager manager, final Location origin) {
    double min = Double.MAX_VALUE;
    final Collection<Killer> killers = manager.getMurderers();
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
