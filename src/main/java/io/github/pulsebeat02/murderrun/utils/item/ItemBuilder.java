package io.github.pulsebeat02.murderrun.utils.item;

import static java.util.Objects.requireNonNull;

import io.github.pulsebeat02.murderrun.utils.AdventureUtils;
import io.github.pulsebeat02.murderrun.utils.PDCUtils;
import io.github.pulsebeat02.murderrun.utils.item.Item.Builder;
import java.util.List;
import java.util.function.Consumer;
import net.kyori.adventure.text.Component;
import org.bukkit.Bukkit;
import org.bukkit.Color;
import org.bukkit.Material;
import org.bukkit.NamespacedKey;
import org.bukkit.attribute.Attribute;
import org.bukkit.attribute.AttributeModifier;
import org.bukkit.attribute.AttributeModifier.Operation;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.entity.Player;
import org.bukkit.inventory.EquipmentSlotGroup;
import org.bukkit.inventory.ItemFactory;
import org.bukkit.inventory.ItemFlag;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.Damageable;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.inventory.meta.LeatherArmorMeta;
import org.bukkit.inventory.meta.PotionMeta;
import org.bukkit.inventory.meta.SkullMeta;
import org.bukkit.persistence.PersistentDataType;
import org.bukkit.potion.PotionType;
import org.checkerframework.checker.nullness.qual.Nullable;

public final class ItemBuilder implements Builder {

  private final ItemStack stack;

  ItemBuilder(final ItemStack stack) {
    this.stack = stack;
  }

  private ItemMeta meta() {
    final ItemMeta meta = this.stack.getItemMeta();
    if (meta == null) {
      final Material fallback = Material.DIAMOND;
      final ItemFactory factory = Bukkit.getItemFactory();
      return requireNonNull(factory.getItemMeta(fallback));
    }
    return meta;
  }

  @Override
  public Builder amount(final int amount) {
    this.stack.setAmount(amount);
    return this;
  }

  @Override
  public Builder name(final Component name) {
    final String legacy = AdventureUtils.serializeComponentToLegacyString(name);
    final ItemMeta meta = this.meta();
    meta.setDisplayName(legacy);
    this.stack.setItemMeta(meta);
    return this;
  }

  @Override
  public Builder lore(final Component lore) {
    final List<String> legacy = AdventureUtils.serializeLoreToLegacyLore(lore);
    final ItemMeta meta = this.meta();
    meta.setLore(legacy);
    this.stack.setItemMeta(meta);
    return this;
  }

  @Override
  public Builder durability(final int durability) {
    final ItemMeta meta = this.meta();
    if (meta instanceof final Damageable damageable) {
      final Material material = this.stack.getType();
      final int max = material.getMaxDurability();
      final int damage = max - durability;
      damageable.setDamage(damage);
      this.stack.setItemMeta(meta);
    }
    return this;
  }

  @Override
  public Builder useOneDurability() {
    final ItemMeta meta = this.meta();
    if (meta instanceof final Damageable damageable) {
      final Material material = this.stack.getType();
      final int maxDamage = material.getMaxDurability();
      final int damage = damageable.getDamage() + 1;
      damageable.setDamage(damage);
      if (damage >= maxDamage) {
        this.stack.setAmount(0);
      }
      this.stack.setItemMeta(meta);
    }
    return this;
  }

  @Override
  public Builder model(final int data) {
    final ItemMeta meta = this.meta();
    meta.setCustomModelData(data);
    this.stack.setItemMeta(meta);
    return this;
  }

  @Override
  public Builder dummyAttribute() {
    final Attribute attribute = Attribute.GENERIC_OXYGEN_BONUS;
    final NamespacedKey key = attribute.getKey();
    final Operation operation = Operation.ADD_NUMBER;
    final EquipmentSlotGroup group = EquipmentSlotGroup.ANY;
    final AttributeModifier modifier = new AttributeModifier(key, 0, operation, group);
    final ItemMeta meta = this.meta();
    meta.addAttributeModifier(attribute, modifier);
    this.stack.setItemMeta(meta);
    return this;
  }

  @Override
  public Builder hideAttributes() {
    this.dummyAttribute();
    final ItemMeta meta = this.meta();
    meta.addItemFlags(
        ItemFlag.HIDE_ATTRIBUTES, ItemFlag.HIDE_ADDITIONAL_TOOLTIP, ItemFlag.HIDE_ENCHANTS);
    this.stack.setItemMeta(meta);
    return this;
  }

  @Override
  public Builder modifier(final Attribute attribute, final double amount) {
    final NamespacedKey key = attribute.getKey();
    final Operation operation = Operation.ADD_NUMBER;
    final EquipmentSlotGroup group = EquipmentSlotGroup.ANY;
    final AttributeModifier modifier = new AttributeModifier(key, amount, operation, group);
    final ItemMeta meta = this.meta();
    meta.addAttributeModifier(attribute, modifier);
    this.stack.setItemMeta(meta);
    return this;
  }

  @Override
  public <P, C> Builder pdc(
      final NamespacedKey key, final PersistentDataType<P, C> type, final C value) {
    PDCUtils.setPersistentDataAttribute(this.stack, key, type, value);
    return this;
  }

  @Override
  public Builder enchantment(final Enchantment enchantment, final int level) {
    this.stack.addUnsafeEnchantment(enchantment, level);
    return this;
  }

  @Override
  public Builder dye(final Color color) {
    final ItemMeta meta = this.meta();
    if (meta instanceof final LeatherArmorMeta leatherArmorMeta) {
      leatherArmorMeta.setColor(color);
      this.stack.setItemMeta(leatherArmorMeta);
    }
    return this;
  }

  @Override
  public Builder head(final Player player) {
    final ItemMeta meta = this.meta();
    if (meta instanceof final SkullMeta skullMeta) {
      skullMeta.setOwningPlayer(player);
      this.stack.setItemMeta(skullMeta);
    }
    return this;
  }

  @Override
  public Builder potionColor(final Color color) {
    final ItemMeta meta = this.meta();
    if (meta instanceof final PotionMeta potionMeta) {
      potionMeta.setColor(color);
      this.stack.setItemMeta(potionMeta);
    }
    return this;
  }

  @Override
  public Builder potion(final PotionType type) {
    final ItemMeta meta = this.meta();
    if (meta instanceof final PotionMeta potionMeta) {
      potionMeta.setBasePotionType(type);
      this.stack.setItemMeta(potionMeta);
    }
    return this;
  }

  @Override
  public Builder consume(final @Nullable Consumer<ItemStack> consumer) {
    if (consumer == null) {
      return this;
    }
    consumer.accept(this.stack);
    return this;
  }

  @Override
  public Builder type(final Material material) {
    this.stack.setType(material);
    return this;
  }

  @Override
  public Builder unbreakable() {
    final ItemMeta meta = this.meta();
    meta.setUnbreakable(true);
    this.stack.setItemMeta(meta);
    return this;
  }

  @Override
  public ItemStack build() {
    return this.stack;
  }
}
