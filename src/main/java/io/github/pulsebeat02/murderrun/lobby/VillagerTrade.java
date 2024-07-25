package io.github.pulsebeat02.murderrun.lobby;

import io.github.pulsebeat02.murderrun.trap.GameTrap;
import org.bukkit.inventory.ItemStack;

import java.util.Map;
import java.util.function.UnaryOperator;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public enum VillagerTrade {
  ;

  private static final Map<String, VillagerTrade> LOOKUP_TABLE =
      Stream.of(VillagerTrade.values())
          .collect(Collectors.toMap(Enum::name, UnaryOperator.identity()));

  private final ItemStack cost;
  private final ItemStack stack;

  VillagerTrade(final ItemStack cost, final ItemStack stack) {
    this.cost = cost;
    this.stack = stack;
  }

  public ItemStack getCost() {
    return this.cost;
  }

  public ItemStack getStack() {
    return this.stack;
  }

  public static VillagerTrade get(final String name) {
    return LOOKUP_TABLE.get(name.toLowerCase());
  }
}
