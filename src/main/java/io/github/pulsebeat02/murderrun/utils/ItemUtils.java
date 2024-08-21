package io.github.pulsebeat02.murderrun.utils;

import static java.util.Objects.requireNonNull;

import io.github.pulsebeat02.murderrun.immutable.Keys;
import io.github.pulsebeat02.murderrun.locale.Message;
import java.util.List;
import net.kyori.adventure.text.Component;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.NamespacedKey;
import org.bukkit.attribute.Attribute;
import org.bukkit.attribute.AttributeModifier;
import org.bukkit.attribute.AttributeModifier.Operation;
import org.bukkit.inventory.EquipmentSlotGroup;
import org.bukkit.inventory.ItemFactory;
import org.bukkit.inventory.ItemFlag;
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

  public static boolean isTrap(final ItemStack stack) {
    return getPersistentDataAttribute(stack, Keys.GADGET_KEY_NAME, PersistentDataType.STRING)
        != null;
  }

  public static ItemStack createKillerArrow() {

    final Component itemName = Message.ARROW_NAME.build();
    final Component itemLore = Message.ARROW_LORE.build();
    final String name = AdventureUtils.serializeComponentToLegacyString(itemName);
    final List<String> rawLore = AdventureUtils.serializeLoreToLegacyLore(itemLore);

    final ItemStack stack = new ItemStack(Material.ARROW);
    final ItemMeta meta = requireNonNull(stack.getItemMeta());
    final Attribute attribute = Attribute.GENERIC_MOVEMENT_SPEED;
    final NamespacedKey key = attribute.getKey();
    final Operation operation = Operation.ADD_NUMBER;
    final EquipmentSlotGroup group = EquipmentSlotGroup.ANY;
    final AttributeModifier modifier = new AttributeModifier(key, 0, operation, group);
    meta.addAttributeModifier(attribute, modifier);
    meta.addItemFlags(ItemFlag.HIDE_ATTRIBUTES, ItemFlag.HIDE_ADDITIONAL_TOOLTIP);
    meta.setDisplayName(name);
    meta.setLore(rawLore);
    stack.setItemMeta(meta);

    return stack;
  }

  public static ItemStack createKillerSword() {

    final ItemStack stack = new ItemStack(Material.DIAMOND_SWORD);
    setPDCTags(stack);

    final Component itemName = Message.KILLER_SWORD.build();
    final String name = AdventureUtils.serializeComponentToLegacyString(itemName);
    final ItemMeta meta = requireNonNull(stack.getItemMeta());
    setAttributeModifiers(meta);
    meta.addItemFlags(ItemFlag.HIDE_ATTRIBUTES, ItemFlag.HIDE_ADDITIONAL_TOOLTIP);
    meta.setDisplayName(name);
    meta.setCustomModelData(1);
    stack.setItemMeta(meta);

    return stack;
  }

  private static void setPDCTags(final ItemStack stack) {
    setPersistentDataAttribute(stack, Keys.SPECIAL_SWORD, PersistentDataType.BOOLEAN, true);
    setPersistentDataAttribute(stack, Keys.CAN_BREAK_BLOCKS, PersistentDataType.BOOLEAN, true);
  }

  private static void setAttributeModifiers(final ItemMeta meta) {
    final Attribute attribute = Attribute.GENERIC_ATTACK_DAMAGE;
    final NamespacedKey key = attribute.getKey();
    final AttributeModifier.Operation operation = Operation.ADD_NUMBER;
    final EquipmentSlotGroup group = EquipmentSlotGroup.ANY;
    final AttributeModifier modifier = new AttributeModifier(key, 16, operation, group);
    meta.addAttributeModifier(attribute, modifier);
  }

  public static <P, C> boolean setPersistentDataAttribute(
      final ItemStack stack,
      final NamespacedKey key,
      final PersistentDataType<P, C> type,
      final C value) {

    ItemMeta meta = stack.getItemMeta();
    if (meta == null) {
      final Material material = stack.getType();
      final ItemFactory factory = Bukkit.getItemFactory();
      meta = factory.getItemMeta(material);
    }

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

    if (stack == null) {
      return null;
    }

    final ItemMeta meta = stack.getItemMeta();
    if (meta == null) {
      return null;
    }

    final PersistentDataContainer container = meta.getPersistentDataContainer();
    return container.get(key, type);
  }
}
