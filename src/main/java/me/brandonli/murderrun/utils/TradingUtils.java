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
package me.brandonli.murderrun.utils;

import static java.util.Objects.requireNonNull;
import static net.kyori.adventure.text.Component.empty;

import java.util.*;
import java.util.stream.Stream;
import me.brandonli.murderrun.game.GameProperties;
import me.brandonli.murderrun.game.ability.Ability;
import me.brandonli.murderrun.game.ability.AbilityRegistry;
import me.brandonli.murderrun.game.ability.killer.KillerAbility;
import me.brandonli.murderrun.game.ability.survivor.SurvivorAbility;
import me.brandonli.murderrun.game.gadget.Gadget;
import me.brandonli.murderrun.game.gadget.GadgetRegistry;
import me.brandonli.murderrun.game.gadget.killer.KillerDevice;
import me.brandonli.murderrun.game.gadget.survivor.SurvivorDevice;
import me.brandonli.murderrun.locale.Message;
import me.brandonli.murderrun.utils.item.Item;
import me.brandonli.murderrun.utils.item.ItemFactory;
import net.kyori.adventure.text.Component;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.MerchantRecipe;
import org.bukkit.inventory.meta.ItemMeta;

public final class TradingUtils {

  private static final Comparator<ItemStack> ITEM_STACK_COMPARATOR =
      Comparator.comparing(TradingUtils::compareStackName);

  private TradingUtils() {
    throw new UnsupportedOperationException("Utility class cannot be instantiated");
  }

  public static Stream<String> getAbilityTradeSuggestions() {
    final AbilityRegistry registry = AbilityRegistry.getRegistry();
    final Collection<Ability> gadgets = registry.getAbilities();
    return gadgets.stream().map(Ability::getId);
  }

  public static List<ItemStack> getAllAbilityRecipes() {
    final AbilityRegistry registry = AbilityRegistry.getRegistry();
    final Collection<Ability> gadgets = registry.getAbilities();
    final List<ItemStack> recipes = new ArrayList<>();
    for (final Ability ability : gadgets) {
      final Item.Builder stack = requireNonNull(ability.getStackBuilder());
      final ItemStack itemStack = stack.build();
      recipes.add(itemStack);
    }
    return recipes;
  }

  public static List<ItemStack> parseAbilityRecipes(final String... args) {
    final AbilityRegistry registry = AbilityRegistry.getRegistry();
    final List<ItemStack> recipes = new ArrayList<>();
    for (final String arg : args) {
      final Ability ability = registry.getAbility(arg);
      if (ability == null) {
        continue;
      }
      final Item.Builder stack = requireNonNull(ability.getStackBuilder());
      final ItemStack itemStack = stack.build();
      recipes.add(itemStack);
    }
    return recipes;
  }

  public static Collection<ItemStack> getAbilityShopItems(final boolean isSurvivorAbilities) {
    final AbilityRegistry registry = AbilityRegistry.getRegistry();
    final Collection<Ability> abilities = registry.getAbilities();
    final Set<ItemStack> items = new TreeSet<>(ITEM_STACK_COMPARATOR);
    for (final Ability ability : abilities) {
      final boolean add = (isSurvivorAbilities && ability instanceof SurvivorAbility)
          || (!isSurvivorAbilities && ability instanceof KillerAbility);
      if (add) {
        final Item.Builder builder = requireNonNull(ability.getStackBuilder());
        final ItemStack stack = builder.build();
        items.add(stack);
      }
    }
    return items;
  }

  public static MerchantRecipe createGadgetRecipe(
      final GameProperties properties, final Gadget gadget) {
    final int cost = gadget.getPrice();
    final Item.Builder stack = requireNonNull(gadget.getStackBuilder());
    final ItemStack itemStack = stack.build();
    final ItemStack ingredient = ItemFactory.createCurrency(properties, cost);
    final int uses = Integer.MAX_VALUE;
    final MerchantRecipe recipe = new MerchantRecipe(itemStack, uses);
    recipe.addIngredient(ingredient);
    return recipe;
  }

  public static Optional<MerchantRecipe> getRecipeByResult(
      final GameProperties properties, final ItemStack item) {
    final GadgetRegistry registry = GadgetRegistry.getRegistry();
    final Collection<Gadget> gadgets = registry.getGadgets();
    MerchantRecipe target = null;
    for (final Gadget gadget : gadgets) {
      final MerchantRecipe recipe = createGadgetRecipe(properties, gadget);
      if (matchesResult(recipe, item)) {
        target = recipe;
        break;
      }
    }
    return Optional.ofNullable(target);
  }

  private static boolean matchesResult(final MerchantRecipe recipe, final ItemStack clickedItem) {
    final ItemStack recipeItem = recipe.getResult();
    return (recipeItem.isSimilar(clickedItem)
        && recipeItem.getType() == clickedItem.getType()
        && recipeItem.hasItemMeta() == clickedItem.hasItemMeta());
  }

  private static String compareStackName(final ItemStack stack) {
    final ItemMeta meta = requireNonNull(stack.getItemMeta());
    final Component component = requireNonNull(meta.displayName());
    final String display = ComponentUtils.serializeComponentToPlain(component);
    return requireNonNull(display);
  }

  public static Stream<String> getGadgetTradeSuggestions() {
    final GadgetRegistry registry = GadgetRegistry.getRegistry();
    final Collection<Gadget> gadgets = registry.getGadgets();
    return gadgets.stream().map(Gadget::getId);
  }

  public static List<MerchantRecipe> getAllGadgetRecipes(final GameProperties properties) {
    final GadgetRegistry registry = GadgetRegistry.getRegistry();
    final Collection<Gadget> gadgets = registry.getGadgets();
    final List<MerchantRecipe> recipes = new ArrayList<>();
    for (final Gadget gadget : gadgets) {
      final MerchantRecipe recipe = createGadgetRecipe(properties, gadget);
      recipes.add(recipe);
    }
    return recipes;
  }

  public static List<MerchantRecipe> parseGadgetRecipes(
      final GameProperties properties, final String... args) {
    final GadgetRegistry registry = GadgetRegistry.getRegistry();
    final List<MerchantRecipe> recipes = new ArrayList<>();
    for (final String arg : args) {
      final Gadget gadget = registry.getGadget(arg);
      if (gadget == null) {
        continue;
      }
      final MerchantRecipe recipe = createGadgetRecipe(properties, gadget);
      recipes.add(recipe);
    }
    return recipes;
  }

  public static Collection<ItemStack> getGadgetShopItems(final boolean isSurvivorGadgets) {
    final GadgetRegistry registry = GadgetRegistry.getRegistry();
    final Collection<Gadget> gadgets = registry.getGadgets();
    final Set<ItemStack> items = new TreeSet<>(ITEM_STACK_COMPARATOR);
    for (final Gadget gadget : gadgets) {
      final boolean add = (isSurvivorGadgets && gadget instanceof SurvivorDevice)
          || (!isSurvivorGadgets && gadget instanceof KillerDevice);
      if (add) {
        final ItemStack stack = getModifiedLoreWithCost(gadget);
        items.add(stack);
      }
    }
    return items;
  }

  public static ItemStack getModifiedLoreWithCost(final Gadget gadget) {
    final Item.Builder stack = requireNonNull(gadget.getStackBuilder());
    final ItemStack itemStack = stack.build();
    final ItemStack clone = itemStack.clone();
    final ItemMeta meta = requireNonNull(clone.getItemMeta());
    List<Component> components = meta.lore();
    if (components == null) {
      components = new ArrayList<>();
    }

    final List<Component> copy = new ArrayList<>(components);
    final int cost = gadget.getPrice();
    final Component component = Message.SHOP_GUI_COST_LORE.build(cost);
    copy.addFirst(empty());
    copy.addFirst(component);
    meta.lore(copy);
    clone.setItemMeta(meta);

    return clone;
  }
}
