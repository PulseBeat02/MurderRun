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

import me.brandonli.murderrun.utils.immutable.Keys;
import org.bukkit.NamespacedKey;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.persistence.PersistentDataContainer;
import org.bukkit.persistence.PersistentDataType;
import org.checkerframework.checker.nullness.qual.Nullable;

public final class PDCUtils {

  private PDCUtils() {
    throw new UnsupportedOperationException("Utility class cannot be instantiated");
  }

  public static boolean isLeaveItem(final ItemStack stack) {
    return attributeExists(stack, Keys.LEAVE, PersistentDataType.BOOLEAN);
  }

  public static boolean isCarPart(final ItemStack stack) {
    return attributeExists(stack, Keys.CAR_PART_UUID, PersistentDataType.STRING);
  }

  public static boolean isSword(final ItemStack stack) {
    return attributeExists(stack, Keys.SPECIAL_SWORD, PersistentDataType.BOOLEAN);
  }

  public static boolean canBreakMapBlocks(final ItemStack stack) {
    return attributeExists(stack, Keys.CAN_BREAK_BLOCKS, PersistentDataType.BOOLEAN);
  }

  public static boolean isGadget(final ItemStack stack) {
    return attributeExists(stack, Keys.GADGET_KEY_NAME, PersistentDataType.STRING);
  }

  public static boolean isFlashBang(final ItemStack stack) {
    return attributeExists(stack, Keys.FLASH_BANG, PersistentDataType.BOOLEAN);
  }

  public static boolean isSmokeGrenade(final ItemStack stack) {
    return attributeExists(stack, Keys.SMOKE_GRENADE, PersistentDataType.BOOLEAN);
  }

  public static boolean isHook(final ItemStack stack) {
    return attributeExists(stack, Keys.HOOK, PersistentDataType.BOOLEAN);
  }

  public static boolean isPortalGun(final ItemStack stack) {
    return attributeExists(stack, Keys.PORTAL_GUN, PersistentDataType.BOOLEAN);
  }

  public static boolean isTrap(final ItemStack stack) {
    return attributeExists(stack, Keys.GADGET_KEY_NAME, PersistentDataType.STRING);
  }

  public static boolean isWand(final ItemStack stack) {
    return attributeExists(stack, Keys.ITEM_WAND, PersistentDataType.BOOLEAN);
  }

  public static boolean isFlashlight(final ItemStack stack) {
    return attributeExists(stack, Keys.FLASHLIGHT, PersistentDataType.BOOLEAN);
  }

  public static boolean isAbility(final ItemStack stack) {
    return attributeExists(stack, Keys.ABILITY_KEY_NAME, PersistentDataType.STRING);
  }

  private static boolean attributeExists(
      final ItemStack stack, final NamespacedKey key, final PersistentDataType<?, ?> type) {
    if (stack == null) {
      return false;
    }
    return getPersistentDataAttribute(stack, key, type) != null;
  }

  public static <P, C> boolean setPersistentDataAttribute(
      final ItemStack stack,
      final NamespacedKey key,
      final PersistentDataType<P, C> type,
      final C value) {
    final ItemMeta meta = stack.getItemMeta();
    if (meta == null || value == null) {
      return false;
    }

    final PersistentDataContainer container = meta.getPersistentDataContainer();
    container.set(key, type, value);
    stack.setItemMeta(meta);

    return true;
  }

  public static <P, C> @Nullable C getPersistentDataAttribute(
      final ItemStack stack, final NamespacedKey key, final PersistentDataType<P, C> type) {
    final ItemMeta meta = stack.getItemMeta();
    if (meta == null) {
      return null;
    }

    final PersistentDataContainer container = meta.getPersistentDataContainer();
    return container.get(key, type);
  }
}
