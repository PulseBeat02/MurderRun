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
package me.brandonli.murderrun.utils.item;

import static java.util.Objects.requireNonNull;

import com.destroystokyo.paper.profile.PlayerProfile;
import java.net.MalformedURLException;
import java.net.URI;
import java.net.URL;
import java.util.List;
import java.util.UUID;
import java.util.function.Consumer;
import me.brandonli.murderrun.utils.ComponentUtils;
import me.brandonli.murderrun.utils.PDCUtils;
import me.brandonli.murderrun.utils.item.Item.Builder;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.TextDecoration;
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
import org.bukkit.inventory.meta.components.UseCooldownComponent;
import org.bukkit.persistence.PersistentDataType;
import org.bukkit.potion.PotionType;
import org.bukkit.profile.PlayerTextures;
import org.checkerframework.checker.nullness.qual.Nullable;

public final class ItemBuilder implements Builder {

  private ItemStack stack;

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
  public Builder nameWithItalics(final Component name) {
    final ItemMeta meta = this.meta();
    this.stack.setItemMeta(meta);
    return this;
  }

  @Override
  public Builder name(final Component name) {
    final ItemMeta meta = this.meta();
    meta.displayName(name.decoration(TextDecoration.ITALIC, false));
    this.stack.setItemMeta(meta);
    return this;
  }

  @Override
  public Builder lore(final Component lore) {
    final List<Component> components = ComponentUtils.wrapLoreLines(lore, 40);
    final ItemMeta meta = this.meta();
    meta.lore(components);
    this.stack.setItemMeta(meta);
    return this;
  }

  @Override
  public Builder lore(final List<Component> lore) {
    final ItemMeta meta = this.meta();
    meta.lore(lore);
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
  public Builder model(final @Nullable String data) {
    final ItemMeta meta = this.meta();
    NamespacedKey key = null;
    if (data != null) {
      key = new NamespacedKey("murderrun", data);
    }
    meta.setItemModel(key);
    this.stack.setItemMeta(meta);
    return this;
  }

  @Override
  public Builder dummyAttribute() {
    final Attribute attribute = Attribute.OXYGEN_BONUS;
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
    final ItemMeta meta = this.meta();
    meta.addItemFlags(
        ItemFlag.HIDE_ATTRIBUTES,
        ItemFlag.HIDE_ENCHANTS,
        ItemFlag.HIDE_STORED_ENCHANTS,
        ItemFlag.HIDE_ARMOR_TRIM,
        ItemFlag.HIDE_UNBREAKABLE,
        ItemFlag.HIDE_DYE);
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
    this.stack = this.stack.withType(material);
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
  public Builder head(final String url) {
    final ItemMeta meta = this.meta();
    if (meta instanceof final SkullMeta skullMeta) {
      try {
        final UUID uuid = UUID.randomUUID();
        final PlayerProfile profile = Bukkit.createProfile(uuid);
        final PlayerTextures textures = profile.getTextures();
        final URI uri = URI.create(url);
        final URL urlObject = uri.toURL();
        textures.setSkin(urlObject);
        profile.setTextures(textures);
        skullMeta.setPlayerProfile(profile);
        this.stack.setItemMeta(skullMeta);
      } catch (final MalformedURLException e) {
        throw new AssertionError(e);
      }
    }
    return this;
  }

  @Override
  public Builder cooldown(final float cooldown, final @Nullable NamespacedKey group) {
    final ItemMeta meta = this.meta();
    final UseCooldownComponent component = meta.getUseCooldown();
    component.setCooldownSeconds(cooldown);
    component.setCooldownGroup(group);
    meta.setUseCooldown(component);
    this.stack.setItemMeta(meta);
    return this;
  }

  @Override
  public ItemStack build() {
    return this.stack;
  }
}
