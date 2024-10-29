package io.github.pulsebeat02.murderrun.utils;

import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.PlayerInventory;

public final class InventoryUtils {

  private InventoryUtils() {
    throw new UnsupportedOperationException("Utility class cannot be instantiated");
  }

  public static ItemStack[] getAllSlotsOnScreen(final Player player) {
    final PlayerInventory inventory = player.getInventory();
    final ItemStack[] contents = inventory.getContents();
    final ItemStack cursor = player.getItemOnCursor();
    final ItemStack[] slots = new ItemStack[contents.length + 1];
    System.arraycopy(contents, 0, slots, 0, contents.length);
    slots[slots.length - 1] = cursor;
    return slots;
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
