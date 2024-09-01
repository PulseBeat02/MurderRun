package io.github.pulsebeat02.murderrun.utils;

import io.github.pulsebeat02.murderrun.game.gadget.Gadget;
import io.github.pulsebeat02.murderrun.game.gadget.GlobalGadgetRegistry;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.stream.Stream;
import org.bukkit.inventory.MerchantRecipe;

public final class TradingUtils {

  private TradingUtils() {
    throw new UnsupportedOperationException("Utility class cannot be instantiated");
  }

  public static Stream<String> getTradeSuggestions() {
    final GlobalGadgetRegistry registry = GlobalGadgetRegistry.getRegistry();
    final Collection<Gadget> gadgets = registry.getGadgets();
    return gadgets.stream().map(Gadget::getName);
  }

  public static List<MerchantRecipe> getAllRecipes() {
    final GlobalGadgetRegistry registry = GlobalGadgetRegistry.getRegistry();
    final Collection<Gadget> gadgets = registry.getGadgets();
    final List<MerchantRecipe> recipes = new ArrayList<>();
    for (final Gadget gadget : gadgets) {
      final MerchantRecipe recipe = gadget.createRecipe();
      recipes.add(recipe);
    }
    return recipes;
  }

  public static List<MerchantRecipe> parseRecipes(final String... args) {
    final GlobalGadgetRegistry registry = GlobalGadgetRegistry.getRegistry();
    final List<MerchantRecipe> recipes = new ArrayList<>();
    for (final String arg : args) {
      final Gadget gadget = registry.getGadget(arg);
      if (gadget != null) {
        final MerchantRecipe recipe = gadget.createRecipe();
        recipes.add(recipe);
      }
    }
    return recipes;
  }
}
