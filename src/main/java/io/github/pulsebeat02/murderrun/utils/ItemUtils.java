package io.github.pulsebeat02.murderrun.utils;

import static java.util.Objects.requireNonNull;

import io.github.pulsebeat02.murderrun.immutable.Keys;
import org.bukkit.Material;
import org.bukkit.NamespacedKey;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.Damageable;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.persistence.PersistentDataContainer;
import org.bukkit.persistence.PersistentDataType;
import org.checkerframework.checker.nullness.qual.Nullable;

public final class ItemUtils {

  private ItemUtils() {
    throw new UnsupportedOperationException("Utility class cannot be instantiated");
  }

  public static boolean setDurability(final ItemStack stack, final int durability) {
    final ItemMeta meta = stack.getItemMeta();
    if (meta instanceof final Damageable damageable) {
      final Material material = stack.getType();
      final int max = material.getMaxDurability();
      final int damage = max - durability;
      damageable.setDamage(damage);
    }
    return stack.setItemMeta(meta);
  }

  public static boolean isCarPart(final ItemStack stack) {
    return getPersistentDataAttribute(stack, Keys.CAR_PART_UUID, PersistentDataType.STRING) != null;
  }

  public static boolean isSword(final ItemStack stack) {
    return getPersistentDataAttribute(stack, Keys.SPECIAL_SWORD, PersistentDataType.BOOLEAN)
        != null;
  }

  public static boolean canBreakMapBlocks(final ItemStack stack) {
    return getPersistentDataAttribute(stack, Keys.CAN_BREAK_BLOCKS, PersistentDataType.BOOLEAN)
        != null;
  }

  public static boolean isGadget(final ItemStack stack) {
    return getPersistentDataAttribute(stack, Keys.GADGET_KEY_NAME, PersistentDataType.STRING)
        != null;
  }

  public static boolean isFlashBang(final ItemStack stack) {
    return getPersistentDataAttribute(stack, Keys.FLASH_BANG, PersistentDataType.BOOLEAN) != null;
  }

  public static boolean isSmokeGrenade(final ItemStack stack) {
    return getPersistentDataAttribute(stack, Keys.SMOKE_GRENADE, PersistentDataType.BOOLEAN)
        != null;
  }

  public static boolean isHook(final ItemStack stack) {
    return getPersistentDataAttribute(stack, Keys.HOOK, PersistentDataType.BOOLEAN) != null;
  }

  public static boolean isPortalGun(final ItemStack stack) {
    return getPersistentDataAttribute(stack, Keys.PORTAL_GUN, PersistentDataType.BOOLEAN) != null;
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
    return true;
  }

  public static <P, C> @Nullable C getPersistentDataAttribute(
      final ItemStack stack, final NamespacedKey key, final PersistentDataType<P, C> type) {
    final ItemMeta meta = requireNonNull(stack.getItemMeta());
    final PersistentDataContainer container = meta.getPersistentDataContainer();
    return container.get(key, type);
  }
}
