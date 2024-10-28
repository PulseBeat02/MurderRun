package io.github.pulsebeat02.murderrun.game.gadget.survivor.utility;

import static java.util.Objects.requireNonNull;

import io.github.pulsebeat02.murderrun.game.Game;
import io.github.pulsebeat02.murderrun.game.GameProperties;
import io.github.pulsebeat02.murderrun.game.gadget.packet.GadgetDropPacket;
import io.github.pulsebeat02.murderrun.game.gadget.packet.GadgetRightClickPacket;
import io.github.pulsebeat02.murderrun.game.gadget.survivor.SurvivorGadget;
import io.github.pulsebeat02.murderrun.game.player.GamePlayer;
import io.github.pulsebeat02.murderrun.game.player.Killer;
import io.github.pulsebeat02.murderrun.game.player.PlayerAudience;
import io.github.pulsebeat02.murderrun.game.player.PlayerManager;
import io.github.pulsebeat02.murderrun.immutable.Keys;
import io.github.pulsebeat02.murderrun.locale.Message;
import io.github.pulsebeat02.murderrun.utils.InventoryUtils;
import io.github.pulsebeat02.murderrun.utils.PDCUtils;
import io.github.pulsebeat02.murderrun.utils.item.ItemFactory;
import java.util.Collection;
import net.kyori.adventure.text.Component;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.NamespacedKey;
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
      GameProperties.KILLER_TRACKER_COST,
      ItemFactory::createKillerTracker
    );
  }

  @Override
  public boolean onGadgetDrop(final GadgetDropPacket packet) {
    return true;
  }

  @Override
  public boolean onGadgetRightClick(final GadgetRightClickPacket packet) {
    final Game game = packet.getGame();
    final PlayerManager manager = game.getPlayerManager();
    final GamePlayer player = packet.getPlayer();
    final Location location = player.getLocation();
    final int distance = (int) Math.round(this.getNearestKillerDistance(manager, location));
    final ItemStack stack = packet.getItemStack();
    final int count = this.increaseAndGetKillerCount(stack);
    final boolean destroy = count >= GameProperties.KILLER_TRACKER_USES;
    if (destroy) {
      final PlayerInventory inventory = player.getInventory();
      InventoryUtils.consumeStack(inventory, stack);
    }

    final PlayerAudience audience = player.getAudience();
    final Component message = Message.KILLER_TRACKER_ACTIVATE.build(distance);
    audience.sendMessage(message);
    audience.playSound(GameProperties.KILLER_TRACKER_SOUND);

    return false;
  }

  private int increaseAndGetKillerCount(final ItemStack stack) {
    final NamespacedKey key = Keys.KILLER_TRACKER;
    final PersistentDataType<Integer, Integer> type = PersistentDataType.INTEGER;
    final Integer val = requireNonNull(PDCUtils.getPersistentDataAttribute(stack, key, type));
    final int count = val + 1;
    PDCUtils.setPersistentDataAttribute(stack, key, type, count);
    return count;
  }

  private double getNearestKillerDistance(final PlayerManager manager, final Location origin) {
    double min = Double.MAX_VALUE;
    final Collection<Killer> killers = manager.getMurderers();
    for (final GamePlayer killer : killers) {
      final Location location = killer.getLocation();
      final double distance = location.distance(origin);
      if (distance < min) {
        min = distance;
      }
    }
    return min;
  }
}
