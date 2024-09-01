package io.github.pulsebeat02.murderrun.reflect;

import org.bukkit.Location;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

public interface PacketToolAPI {

  byte[] toByteArray(final ItemStack item);

  ItemStack fromByteArray(final byte[] bytes);

  void setEntityGlowing(final Entity entity, final Player watcher, final boolean glowing);

  void setBlockGlowing(final Player watcher, final Location target, final boolean glowing);

  void injectNettyHandler(final String key, final Object handler);
}
