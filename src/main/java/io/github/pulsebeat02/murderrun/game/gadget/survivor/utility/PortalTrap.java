package io.github.pulsebeat02.murderrun.game.gadget.survivor.utility;

import static java.util.Objects.requireNonNull;

import io.github.pulsebeat02.murderrun.game.Game;
import io.github.pulsebeat02.murderrun.game.gadget.GadgetManager;
import io.github.pulsebeat02.murderrun.game.gadget.GameProperties;
import io.github.pulsebeat02.murderrun.game.gadget.survivor.SurvivorGadget;
import io.github.pulsebeat02.murderrun.game.player.GamePlayer;
import io.github.pulsebeat02.murderrun.game.player.PlayerManager;
import io.github.pulsebeat02.murderrun.locale.Message;
import io.github.pulsebeat02.murderrun.utils.PDCUtils;
import java.util.ArrayList;
import java.util.Collection;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.World;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Item;
import org.bukkit.inventory.ItemStack;
import org.checkerframework.checker.nullness.qual.Nullable;

public final class PortalTrap extends SurvivorGadget {

  public PortalTrap() {
    super(
        "portal",
        Material.PURPLE_WOOL,
        Message.PORTAL_NAME.build(),
        Message.PORTAL_LORE.build(),
        GameProperties.PORTAL_TRAP_COST);
  }

  @Override
  public boolean onGadgetDrop(
      final Game game, final GamePlayer player, final Item item, final boolean remove) {

    final GadgetManager gadgetManager = game.getGadgetManager();
    final double range = gadgetManager.getActivationRange();
    final Location location = player.getLocation();
    final World world = requireNonNull(location.getWorld());
    final Collection<Item> items = this.getTrapItemStackEntities(location, world, range);
    final Item closest = this.getClosestEntity(location, items);
    if (closest == null) {
      return true;
    }

    final PlayerManager playerManager = game.getPlayerManager();
    final GamePlayer killer = requireNonNull(playerManager.getNearestKiller(location));
    final Location killerLocation = killer.getLocation();
    closest.teleport(killerLocation);

    return super.onGadgetDrop(game, player, item, true);
  }

  private @Nullable Item getClosestEntity(final Location location, final Collection<Item> items) {
    Item closest = null;
    double closestDistance = Double.MAX_VALUE;
    for (final Item item : items) {
      final Location itemLocation = item.getLocation();
      final double distance = location.distanceSquared(itemLocation);
      if (distance < closestDistance) {
        closest = item;
        closestDistance = distance;
      }
    }
    return closest;
  }

  private Collection<Item> getTrapItemStackEntities(
      final Location location, final World world, final double range) {
    final Collection<Entity> entities = world.getNearbyEntities(location, range, range, range);
    final Collection<Item> trapEntities = new ArrayList<>();
    for (final Entity entity : entities) {
      if (!(entity instanceof final Item item)) {
        continue;
      }
      final ItemStack stack = item.getItemStack();
      if (PDCUtils.isGadget(stack)) {
        trapEntities.add(item);
      }
    }
    return trapEntities;
  }
}
