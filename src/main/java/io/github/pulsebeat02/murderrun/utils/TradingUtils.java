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
package io.github.pulsebeat02.murderrun.utils;

import static java.util.Objects.requireNonNull;

import io.github.pulsebeat02.murderrun.game.gadget.Gadget;
import io.github.pulsebeat02.murderrun.game.gadget.GadgetRegistry;
import io.github.pulsebeat02.murderrun.game.gadget.killer.KillerApparatus;
import io.github.pulsebeat02.murderrun.game.gadget.survivor.SurvivorApparatus;
import io.github.pulsebeat02.murderrun.locale.Message;
import java.util.*;
import java.util.stream.Stream;
import net.kyori.adventure.text.Component;
import org.bukkit.ChatColor;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.MerchantRecipe;
import org.bukkit.inventory.meta.ItemMeta;

public final class TradingUtils {

  private static final Comparator<ItemStack> ITEM_STACK_COMPARATOR = Comparator.comparing(TradingUtils::compareStackName);

  private static String compareStackName(final ItemStack stack) {
    final ItemMeta meta = requireNonNull(stack.getItemMeta());
    final String display = requireNonNull(meta.getDisplayName());
    return requireNonNull(ChatColor.stripColor(display));
  }

  private TradingUtils() {
    throw new UnsupportedOperationException("Utility class cannot be instantiated");
  }

  public static Stream<String> getTradeSuggestions() {
    final GadgetRegistry registry = GadgetRegistry.getRegistry();
    final Collection<Gadget> gadgets = registry.getGadgets();
    return gadgets.stream().map(Gadget::getName);
  }

  public static List<MerchantRecipe> getAllRecipes() {
    final GadgetRegistry registry = GadgetRegistry.getRegistry();
    final Collection<Gadget> gadgets = registry.getGadgets();
    final List<MerchantRecipe> recipes = new ArrayList<>();
    for (final Gadget gadget : gadgets) {
      final MerchantRecipe recipe = gadget.createRecipe();
      recipes.add(recipe);
    }
    return recipes;
  }

  public static List<MerchantRecipe> parseRecipes(final String... args) {
    final GadgetRegistry registry = GadgetRegistry.getRegistry();
    final List<MerchantRecipe> recipes = new ArrayList<>();
    for (final String arg : args) {
      final Gadget gadget = registry.getGadget(arg);
      if (gadget == null) {
        continue;
      }
      final MerchantRecipe recipe = gadget.createRecipe();
      recipes.add(recipe);
    }
    return recipes;
  }

  public static Collection<ItemStack> getShopItems(final boolean isSurvivorGadgets) {
    final GadgetRegistry registry = GadgetRegistry.getRegistry();
    final Collection<Gadget> gadgets = registry.getGadgets();
    final Set<ItemStack> items = new TreeSet<>(ITEM_STACK_COMPARATOR);
    for (final Gadget gadget : gadgets) {
      final boolean add =
        (isSurvivorGadgets && gadget instanceof SurvivorApparatus) || (!isSurvivorGadgets && gadget instanceof KillerApparatus);
      if (add) {
        final ItemStack stack = getModifiedLoreWithCost(gadget);
        items.add(stack);
      }
    }
    return items;
  }

  private static ItemStack getModifiedLoreWithCost(final Gadget gadget) {
    final ItemStack stack = gadget.getGadget();
    final ItemStack clone = stack.clone();
    final ItemMeta meta = requireNonNull(clone.getItemMeta());
    List<String> lore = meta.getLore();
    if (lore == null) {
      lore = new ArrayList<>();
    }

    final List<String> copy = new ArrayList<>(lore);
    final String raw = getLegacyComponent(gadget);
    copy.addFirst("");
    copy.addFirst(raw);
    meta.setLore(copy);
    clone.setItemMeta(meta);

    return clone;
  }

  private static String getLegacyComponent(final Gadget gadget) {
    final int cost = gadget.getPrice();
    final Component full = Message.SHOP_GUI_COST_LORE.build(cost);
    return ComponentUtils.serializeComponentToLegacyString(full);
  }
}
