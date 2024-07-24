package io.github.pulsebeat02.murderrun.trap;

import org.bukkit.inventory.ItemStack;

import java.util.Map;
import java.util.function.UnaryOperator;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public enum MurderTrap {
  ;

  private static final Map<String, MurderTrap> LOOKUP_TABLE =
      Stream.of(MurderTrap.values())
          .collect(Collectors.toMap(Enum::name, UnaryOperator.identity()));

  private final ItemStack cost;
  private final ItemStack stack;

  MurderTrap(final ItemStack cost, final ItemStack stack) {
    this.cost = cost;
    this.stack = stack;
  }

  public ItemStack getCost() {
    return this.cost;
  }

  public ItemStack getStack() {
    return this.stack;
  }

  public static MurderTrap get(final String name) {
    return LOOKUP_TABLE.get(name.toLowerCase());
  }
}
