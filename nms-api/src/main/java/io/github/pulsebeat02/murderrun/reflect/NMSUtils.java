package io.github.pulsebeat02.murderrun.reflect;

import org.bukkit.inventory.ItemStack;

public interface NMSUtils {

  byte[] toByteArray(final ItemStack item);

  ItemStack fromByteArray(final byte[] bytes);
}
