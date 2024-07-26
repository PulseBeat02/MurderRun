package io.github.pulsebeat02.murderrun.immutable;

import org.bukkit.Bukkit;
import org.bukkit.NamespacedKey;
import org.bukkit.Server;
import org.bukkit.plugin.Plugin;
import org.bukkit.plugin.PluginManager;

public final class NamespacedKeys {

  private static final Plugin PLUGIN;

  static {
    final Server server = Bukkit.getServer();
    final PluginManager manager = server.getPluginManager();
    PLUGIN = manager.getPlugin("murder_run");
  }

  public static final NamespacedKey SPECIAL_SWORD = createKey("sword");
  public static final NamespacedKey CAR_PART_UUID = createKey("car-part-uuid");
  public static final NamespacedKey TRAP_KEY_NAME = createKey("trap");

  private static NamespacedKey createKey(final String key) {
    return new NamespacedKey(PLUGIN, key);
  }
}
