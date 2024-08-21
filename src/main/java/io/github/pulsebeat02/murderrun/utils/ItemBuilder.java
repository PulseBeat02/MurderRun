package io.github.pulsebeat02.murderrun.utils;

import static java.util.Objects.requireNonNull;

import java.util.List;
import java.util.function.Consumer;
import net.kyori.adventure.text.Component;
import org.bukkit.Color;
import org.bukkit.Material;
import org.bukkit.NamespacedKey;
import org.bukkit.attribute.Attribute;
import org.bukkit.attribute.AttributeModifier;
import org.bukkit.attribute.AttributeModifier.Operation;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.entity.Player;
import org.bukkit.inventory.EquipmentSlotGroup;
import org.bukkit.inventory.ItemFlag;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.Damageable;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.inventory.meta.LeatherArmorMeta;
import org.bukkit.inventory.meta.SkullMeta;
import org.bukkit.persistence.PersistentDataType;
import org.checkerframework.checker.nullness.qual.Nullable;

public final class ItemBuilder {

  public static final ItemStack AIR_STACK = create(Material.AIR);

  private ItemBuilder() {
    throw new UnsupportedOperationException("Utility class cannot be instantiated");
  }

  public static ItemStack create(final Material material) {
    return builder(material).build();
  }

  public static Builder builder(final Material material) {
    return builder(new ItemStack(material));
  }

  public static Builder builder(final ItemStack stack) {
    return new Builder(stack);
  }

  public static final class Builder {

    private final ItemStack stack;

    Builder(final ItemStack stack) {
      this.stack = stack;
    }

    private ItemMeta meta() {
      return requireNonNull(this.stack.getItemMeta());
    }

    public Builder amount(final int amount) {
      this.stack.setAmount(amount);
      return this;
    }

    public Builder name(final Component name) {
      final String legacy = AdventureUtils.serializeComponentToLegacyString(name);
      final ItemMeta meta = this.meta();
      meta.setDisplayName(legacy);
      this.stack.setItemMeta(meta);
      return this;
    }

    public Builder lore(final Component lore) {
      final List<String> legacy = AdventureUtils.serializeLoreToLegacyLore(lore);
      final ItemMeta meta = this.meta();
      meta.setLore(legacy);
      this.stack.setItemMeta(meta);
      return this;
    }

    public Builder durability(final int durability) {
      final ItemMeta meta = this.meta();
      if (meta instanceof final Damageable damageable) {
        final Material material = this.stack.getType();
        final int max = material.getMaxDurability();
        final int damage = max - durability;
        damageable.setDamage(damage);
      }
      return this;
    }

    public Builder model(final int data) {
      final ItemMeta meta = this.meta();
      meta.setCustomModelData(data);
      this.stack.setItemMeta(meta);
      return this;
    }

    public Builder dummyAttribute() {
      final Attribute attribute = Attribute.GENERIC_MOVEMENT_SPEED;
      final NamespacedKey key = attribute.getKey();
      final Operation operation = Operation.ADD_NUMBER;
      final EquipmentSlotGroup group = EquipmentSlotGroup.ANY;
      final AttributeModifier modifier = new AttributeModifier(key, 0, operation, group);
      final ItemMeta meta = this.meta();
      meta.addAttributeModifier(attribute, modifier);
      this.stack.setItemMeta(meta);
      return this;
    }

    public Builder hideAttributes() {
      this.dummyAttribute();
      final ItemMeta meta = this.meta();
      meta.addItemFlags(ItemFlag.HIDE_ATTRIBUTES, ItemFlag.HIDE_ADDITIONAL_TOOLTIP);
      this.stack.setItemMeta(meta);
      return this;
    }

    public Builder modifier(final Attribute attribute, final int amount) {
      final NamespacedKey key = attribute.getKey();
      final Operation operation = Operation.ADD_NUMBER;
      final EquipmentSlotGroup group = EquipmentSlotGroup.ANY;
      final AttributeModifier modifier = new AttributeModifier(key, amount, operation, group);
      final ItemMeta meta = this.meta();
      meta.addAttributeModifier(attribute, modifier);
      this.stack.setItemMeta(meta);
      return this;
    }

    public <P, C> Builder pdc(
        final NamespacedKey key, final PersistentDataType<P, C> type, final C value) {
      PDCUtils.setPersistentDataAttribute(this.stack, key, type, value);
      return this;
    }

    public Builder enchantment(final Enchantment enchantment, final int level) {
      this.stack.addEnchantment(enchantment, level);
      return this;
    }

    public Builder dye(final Color color) {
      final ItemMeta meta = this.meta();
      if (meta instanceof final LeatherArmorMeta leatherArmorMeta) {
        leatherArmorMeta.setColor(color);
        this.stack.setItemMeta(leatherArmorMeta);
      }
      return this;
    }

    public Builder head(final Player player) {
      final ItemMeta meta = this.meta();
      if (meta instanceof final SkullMeta skullMeta) {
        skullMeta.setOwningPlayer(player);
        this.stack.setItemMeta(skullMeta);
      }
      return this;
    }

    public Builder consume(final @Nullable Consumer<ItemStack> consumer) {
      if (consumer == null) {
        return this;
      }
      consumer.accept(this.stack);
      return this;
    }

    public ItemStack build() {
      return this.stack;
    }
  }
}
