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
