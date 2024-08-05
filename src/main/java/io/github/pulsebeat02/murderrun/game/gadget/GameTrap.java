package io.github.pulsebeat02.murderrun.game.gadget;

import org.bukkit.inventory.ItemStack;

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
