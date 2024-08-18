package io.github.pulsebeat02.murderrun.utils;

import io.github.pulsebeat02.murderrun.game.gadget.Gadget;
import io.github.pulsebeat02.murderrun.game.gadget.GadgetLoadingMechanism;
import java.lang.invoke.MethodHandle;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Map;
import java.util.stream.Stream;
import org.bukkit.inventory.MerchantRecipe;
import org.incendo.cloud.type.tuple.Pair;

public final class TradingUtils {

  private TradingUtils() {
    throw new UnsupportedOperationException("Utility class cannot be instantiated");
  }

  public static Stream<String> getTradeSuggestions() {
    final Collection<Pair<Gadget, MethodHandle>> values =
        GadgetLoadingMechanism.getGadgetLookUpMap().values();
    return values.stream().map(pair -> {
      final Gadget gadget = pair.first();
      return gadget.getName();
    });
  }

  public static List<MerchantRecipe> getAllRecipes() {
    final Map<String, Pair<Gadget, MethodHandle>> gadgets =
        GadgetLoadingMechanism.getGadgetLookUpMap();
    final Collection<Pair<Gadget, MethodHandle>> values = gadgets.values();
    final List<MerchantRecipe> recipes = new ArrayList<>();
    for (final Pair<Gadget, MethodHandle> pair : values) {
      final Gadget gadget = pair.first();
      final MerchantRecipe recipe = gadget.createRecipe();
      recipes.add(recipe);
    }
    return recipes;
  }

  public static List<MerchantRecipe> parseRecipes(final String[] args) {
    final Map<String, Pair<Gadget, MethodHandle>> gadgets =
        GadgetLoadingMechanism.getGadgetLookUpMap();
    final List<MerchantRecipe> recipes = new ArrayList<>();
    for (final String arg : args) {
      final Pair<Gadget, MethodHandle> pair = gadgets.get(arg);
      if (pair != null) {
        final Gadget gadget = pair.first();
        final MerchantRecipe recipe = gadget.createRecipe();
        recipes.add(recipe);
      }
    }
    return recipes;
  }
}
