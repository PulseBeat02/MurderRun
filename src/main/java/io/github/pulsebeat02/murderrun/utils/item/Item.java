/*

MIT License

Copyright (c) 2024 Brandon Li

Permission is hereby granted, free of charge, to any person obtaining a copy
of this software and associated documentation files (the "Software"), to deal
in the Software without restriction, including without limitation the rights
to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
copies of the Software, and to permit persons to whom the Software is
furnished to do so, subject to the following conditions:

The above copyright notice and this permission notice shall be included in all
copies or substantial portions of the Software.

THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE
SOFTWARE.

*/
package io.github.pulsebeat02.murderrun.utils.item;

import java.util.List;
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

    Builder lore(final List<Component> lore);

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

    Builder head(final String url);

    Builder cooldown(final float cooldown, final @Nullable NamespacedKey group);

    ItemStack build();
  }
}
