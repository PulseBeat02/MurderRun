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
package me.brandonli.murderrun.utils;

import static java.util.Objects.requireNonNull;

import java.util.Collection;
import java.util.Collections;
import java.util.Map;
import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.entity.HumanEntity;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.PlayerInventory;

public final class InventoryUtils {

  private InventoryUtils() {
    throw new UnsupportedOperationException("Utility class cannot be instantiated");
  }

  public static boolean addItem(final HumanEntity player, final ItemStack stack) {
    return addItems(player, Collections.singleton(stack));
  }

  public static boolean addItems(final HumanEntity player, final Collection<ItemStack> stacks) {
    final PlayerInventory inventory = player.getInventory();
    final Location location = player.getLocation();
    final World world = requireNonNull(location.getWorld());
    boolean allAdded = true;
    for (final ItemStack stack : stacks) {
      final Map<Integer, ItemStack> leftover = inventory.addItem(stack);
      if (!leftover.isEmpty()) {
        allAdded = false;
        final Collection<ItemStack> leftovers = leftover.values();
        leftovers.forEach(item -> world.dropItemNaturally(location, item));
      }
    }
    return allAdded;
  }

  public static ItemStack[] getAllSlotsOnScreen(final Player player) {
    final PlayerInventory inventory = player.getInventory();
    @SuppressWarnings("all") // checker
    final ItemStack[] contents = inventory.getContents();
    final ItemStack cursor = player.getItemOnCursor();
    final ItemStack[] slots = new ItemStack[contents.length + 1];
    System.arraycopy(contents, 0, slots, 0, contents.length);
    slots[slots.length - 1] = cursor;
    return slots;
  }

  public static boolean consumeStack(final PlayerInventory inventory, final ItemStack stack) {
    @SuppressWarnings("all") // checker
    final ItemStack[] contents = inventory.getContents();
    for (int i = 0; i < contents.length; i++) {
      final ItemStack item = contents[i];
      if (item == null || !item.isSimilar(stack)) {
        continue;
      }
      final int amount = item.getAmount();
      if (amount == 1) {
        inventory.setItem(i, null);
      } else {
        item.setAmount(amount - 1);
      }
      return true;
    }
    return false;
  }
}
