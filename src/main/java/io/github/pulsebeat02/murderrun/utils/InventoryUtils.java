package io.github.pulsebeat02.murderrun.utils;

import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.PlayerInventory;

public final class InventoryUtils {

  private InventoryUtils() {
    throw new UnsupportedOperationException("Utility class cannot be instantiated");
  }

  public static boolean consumeStack(final PlayerInventory inventory, final ItemStack stack) {
    final ItemStack[] contents = inventory.getContents();
    for (int i = 0; i < contents.length; i++) {
      final ItemStack item = contents[i];
      if (item == null || !item.isSimilar(stack)) {
        continue;
      }
      final int amount = item.getAmount();
      if (amount == 1) {
        inventory.setItem(i, null);
      } else {
        item.setAmount(amount - 1);
      }
      return true;
    }
    return false;
  }
}
