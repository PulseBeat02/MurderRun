package io.github.pulsebeat02.murderrun.reflect;

import org.bukkit.inventory.ItemStack;

public interface PacketToolAPI {

  byte[] toByteArray(final ItemStack item);

  ItemStack fromByteArray(final byte[] bytes);
}
