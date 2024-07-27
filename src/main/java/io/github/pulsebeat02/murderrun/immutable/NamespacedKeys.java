package io.github.pulsebeat02.murderrun.immutable;

import org.bukkit.Bukkit;
import org.bukkit.NamespacedKey;
import org.bukkit.Server;
import org.bukkit.plugin.Plugin;
import org.bukkit.plugin.PluginManager;

public final class NamespacedKeys {

  private static final Plugin PLUGIN;
  public static final NamespacedKey SPECIAL_SWORD = createKey("sword");
  public static final NamespacedKey CAR_PART_UUID = createKey("car-part-uuid");
  public static final NamespacedKey TRAP_KEY_NAME = createKey("trap");

  static {
    final Server server = Bukkit.getServer();
    final PluginManager manager = server.getPluginManager();
    final Plugin plugin = manager.getPlugin("MurderRun");
    if (plugin == null) {
      throw new AssertionError("Failed to retrieve plugin class!");
    }
    PLUGIN = plugin;
  }

  private static NamespacedKey createKey(final String key) {
    return new NamespacedKey(PLUGIN, key);
  }
}
