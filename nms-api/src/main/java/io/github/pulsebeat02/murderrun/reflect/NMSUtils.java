package io.github.pulsebeat02.murderrun.reflect;

import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

public interface NMSUtils {

  byte[] toByteArray(final ItemStack item);

  ItemStack fromByteArray(final byte[] bytes);

  void sendGlowPacket(final Player watcher, final Entity glow);

  void sendRemoveGlowPacket(final Player watcher, final Entity glow);
}
