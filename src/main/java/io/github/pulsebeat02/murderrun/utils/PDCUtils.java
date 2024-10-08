package io.github.pulsebeat02.murderrun.utils;

import io.github.pulsebeat02.murderrun.immutable.Keys;
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

  private static boolean attributeExists(
    final ItemStack stack,
    final NamespacedKey key,
    final PersistentDataType<?, ?> type
  ) {
    if (stack == null) {
      return false;
    }
    return getPersistentDataAttribute(stack, key, type) != null;
  }

  public static <P, C> boolean setPersistentDataAttribute(
    final ItemStack stack,
    final NamespacedKey key,
    final PersistentDataType<P, C> type,
    final C value
  ) {
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
    final ItemStack stack,
    final NamespacedKey key,
    final PersistentDataType<P, C> type
  ) {
    final ItemMeta meta = stack.getItemMeta();
    if (meta == null) {
      return null;
    }

    final PersistentDataContainer container = meta.getPersistentDataContainer();
    return container.get(key, type);
  }
}
