package io.github.pulsebeat02.murderrun.utils;

import org.bukkit.NamespacedKey;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.persistence.PersistentDataContainer;
import org.bukkit.persistence.PersistentDataType;
import org.checkerframework.checker.nullness.qual.Nullable;

public final class ItemStackUtils {

  private ItemStackUtils() {
    throw new UnsupportedOperationException("Utility class cannot be instantiated");
  }

  public static boolean isCarPart(final ItemStack stack) {
    return getData(stack, NamespacedKeys.CAR_PART_UUID, PersistentDataType.STRING) != null;
  }

  public static boolean isSword(final ItemStack stack) {
    return getData(stack, NamespacedKeys.SPECIAL_SWORD, PersistentDataType.BOOLEAN) != null;
  }

  public static boolean canBreakMapBlocks(final ItemStack stack) {
    return getData(stack, NamespacedKeys.CAN_BREAK_BLOCKS, PersistentDataType.BOOLEAN) != null;
  }

  public static boolean isTrap(final ItemStack stack) {
    return getData(stack, NamespacedKeys.TRAP_KEY_NAME, PersistentDataType.STRING) != null;
  }

  public static boolean isFlashBang(final ItemStack stack) {
    return getData(stack, NamespacedKeys.FLASH_BANG, PersistentDataType.BOOLEAN) != null;
  }

  public static boolean isSmokeGrenade(final ItemStack stack) {
    return getData(stack, NamespacedKeys.SMOKE_GRENADE, PersistentDataType.BOOLEAN) != null;
  }

  public static <P, C> @Nullable C getData(
      final ItemStack stack, final NamespacedKey key, final PersistentDataType<P, C> type) {
    final ItemMeta meta = stack.getItemMeta();
    if (meta == null) {
      return null;
    }
    final PersistentDataContainer container = meta.getPersistentDataContainer();
    return container.get(key, type);
  }
}
