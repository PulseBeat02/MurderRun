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
package me.brandonli.murderrun.game.gadget.survivor.utility;

import static java.util.Objects.requireNonNull;

import java.util.ArrayList;
import java.util.Collection;
import me.brandonli.murderrun.game.Game;
import me.brandonli.murderrun.game.GameProperties;
import me.brandonli.murderrun.game.gadget.GadgetManager;
import me.brandonli.murderrun.game.gadget.packet.GadgetDropPacket;
import me.brandonli.murderrun.game.gadget.survivor.SurvivorGadget;
import me.brandonli.murderrun.game.player.GamePlayer;
import me.brandonli.murderrun.game.player.GamePlayerManager;
import me.brandonli.murderrun.locale.Message;
import me.brandonli.murderrun.utils.PDCUtils;
import me.brandonli.murderrun.utils.item.ItemFactory;
import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Item;
import org.bukkit.inventory.ItemStack;
import org.checkerframework.checker.nullness.qual.Nullable;

public final class PortalTrap extends SurvivorGadget {

  public PortalTrap() {
    super(
      "portal_trap",
      GameProperties.PORTAL_TRAP_COST,
      ItemFactory.createGadget("portal_trap", GameProperties.PORTAL_TRAP_MATERIAL, Message.PORTAL_NAME.build(), Message.PORTAL_LORE.build())
    );
  }

  @Override
  public boolean onGadgetDrop(final GadgetDropPacket packet) {
    final Game game = packet.getGame();
    final GamePlayer player = packet.getPlayer();
    final Item item = packet.getItem();

    final GadgetManager gadgetManager = game.getGadgetManager();
    final double range = gadgetManager.getActivationRange();
    final Location location = player.getLocation();
    final World world = requireNonNull(location.getWorld());
    final Collection<Item> items = this.getTrapItemStackEntities(location, world, range);
    final Item closest = this.getClosestEntity(location, items);
    if (closest == null) {
      return true;
    }

    final GamePlayerManager playerManager = game.getPlayerManager();
    final GamePlayer killer = requireNonNull(playerManager.getNearestKiller(location));
    final Location killerLocation = killer.getLocation();
    closest.teleport(killerLocation);
    item.remove();

    return false;
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

  private Collection<Item> getTrapItemStackEntities(final Location location, final World world, final double range) {
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
