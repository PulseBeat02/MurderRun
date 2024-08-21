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
import org.bukkit.inventory.ItemFlag;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.Damageable;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.persistence.PersistentDataType;

public final class ItemFactory {

  private ItemFactory() {
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

  public static ItemMeta createMeta(final ItemStack stack) {
    ItemMeta meta = stack.getItemMeta();
    if (meta == null) {
      final Material material = stack.getType();
      final org.bukkit.inventory.ItemFactory factory = Bukkit.getItemFactory();
      meta = factory.getItemMeta(material);
    }
    return requireNonNull(meta);
  }

  public static ItemStack createCurrency() {
    final ItemStack stack = new ItemStack(Material.NETHER_STAR);
    final ItemMeta meta = createMeta(stack);
    final Component itemName = Message.MINEBUCKS.build();
    final String name = AdventureUtils.serializeComponentToLegacyString(itemName);
    meta.setDisplayName(name);
    meta.setCustomModelData(1);
    stack.setItemMeta(meta);
    return stack;
  }

  public static ItemStack createKillerArrow() {

    final Component itemName = Message.ARROW_NAME.build();
    final Component itemLore = Message.ARROW_LORE.build();
    final String name = AdventureUtils.serializeComponentToLegacyString(itemName);
    final List<String> rawLore = AdventureUtils.serializeLoreToLegacyLore(itemLore);
    final ItemStack stack = new ItemStack(Material.ARROW);
    final ItemMeta meta = requireNonNull(stack.getItemMeta());
    hideAllAttributes(meta);
    meta.setDisplayName(name);
    meta.setLore(rawLore);
    stack.setItemMeta(meta);

    return stack;
  }

  public static ItemStack createKillerSword() {

    final ItemStack stack = new ItemStack(Material.DIAMOND_SWORD);
    final Component itemName = Message.KILLER_SWORD.build();
    final String name = AdventureUtils.serializeComponentToLegacyString(itemName);
    final ItemMeta meta = requireNonNull(stack.getItemMeta());
    final Attribute attribute = Attribute.GENERIC_ATTACK_DAMAGE;
    final NamespacedKey key = attribute.getKey();
    final AttributeModifier.Operation operation = Operation.ADD_NUMBER;
    final EquipmentSlotGroup group = EquipmentSlotGroup.ANY;
    final AttributeModifier modifier = new AttributeModifier(key, 10, operation, group);
    meta.addAttributeModifier(attribute, modifier);
    meta.addItemFlags(ItemFlag.HIDE_ATTRIBUTES, ItemFlag.HIDE_ADDITIONAL_TOOLTIP);
    meta.setDisplayName(name);
    meta.setCustomModelData(1);
    stack.setItemMeta(meta);

    PDCUtils.setPersistentDataAttribute(
        stack, Keys.SPECIAL_SWORD, PersistentDataType.BOOLEAN, true);
    PDCUtils.setPersistentDataAttribute(
        stack, Keys.CAN_BREAK_BLOCKS, PersistentDataType.BOOLEAN, true);

    return stack;
  }

  public static void hideAllAttributes(final ItemMeta meta) {
    final Attribute attribute = Attribute.GENERIC_MOVEMENT_SPEED;
    final NamespacedKey key = attribute.getKey();
    final Operation operation = Operation.ADD_NUMBER;
    final EquipmentSlotGroup group = EquipmentSlotGroup.ANY;
    final AttributeModifier modifier = new AttributeModifier(key, 0, operation, group);
    meta.addAttributeModifier(attribute, modifier);
    meta.addItemFlags(ItemFlag.HIDE_ATTRIBUTES, ItemFlag.HIDE_ADDITIONAL_TOOLTIP);
  }
}
