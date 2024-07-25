package io.github.pulsebeat02.murderrun.trap;

import org.bukkit.inventory.ItemStack;

import java.util.Map;
import java.util.function.UnaryOperator;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public enum GameTrap {
  ;

  private final ItemStack cost;
  private final ItemStack stack;

  GameTrap(final ItemStack cost, final ItemStack stack) {
    this.cost = cost;
    this.stack = stack;
  }

  public ItemStack getCost() {
    return this.cost;
  }

  public ItemStack getStack() {
    return this.stack;
  }
}