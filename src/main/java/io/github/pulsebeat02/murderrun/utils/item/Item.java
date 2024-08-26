package io.github.pulsebeat02.murderrun.utils.item;

import java.util.function.Consumer;
import net.kyori.adventure.text.Component;
import org.bukkit.Color;
import org.bukkit.Material;
import org.bukkit.NamespacedKey;
import org.bukkit.attribute.Attribute;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.persistence.PersistentDataType;
import org.bukkit.potion.PotionType;
import org.checkerframework.checker.nullness.qual.Nullable;

public interface Item {

  ItemStack AIR_STACK = create(Material.AIR);

  static ItemStack create(final Material material) {
    return builder(material).build();
  }

  static Builder builder(final Material material) {
    return builder(new ItemStack(material));
  }

  static Builder builder(final ItemStack stack) {
    return new ItemBuilder(stack);
  }

  interface Builder {

    Builder amount(final int amount);

    Builder name(final Component name);

    Builder lore(final Component lore);

    Builder durability(final int durability);

    Builder useOneDurability();

    Builder model(final int data);

    Builder dummyAttribute();

    Builder hideAttributes();

    Builder modifier(final Attribute attribute, final double amount);

    <P, C> Builder pdc(final NamespacedKey key, final PersistentDataType<P, C> type, final C value);

    Builder enchantment(final Enchantment enchantment, final int level);

    Builder dye(final Color color);

    Builder head(final Player player);

    Builder potionColor(final Color color);

    Builder potion(final PotionType type);

    Builder consume(final @Nullable Consumer<ItemStack> consumer);

    Builder type(final Material material);

    Builder unbreakable();

    ItemStack build();
  }
}
