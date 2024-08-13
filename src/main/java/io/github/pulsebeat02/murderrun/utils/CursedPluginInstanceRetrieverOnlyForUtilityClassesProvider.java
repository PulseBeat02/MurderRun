package io.github.pulsebeat02.murderrun.utils;

import org.bukkit.Bukkit;
import org.bukkit.Server;
import org.bukkit.plugin.Plugin;
import org.bukkit.plugin.PluginManager;

public final class CursedPluginInstanceRetrieverOnlyForUtilityClassesProvider {

  private static final String PLUGIN_NAME = "MurderRun";
  private static final Plugin PLUGIN_INSTANCE;

  static {
    final Server server = Bukkit.getServer();
    final PluginManager manager = server.getPluginManager();
    PLUGIN_INSTANCE = manager.getPlugin(PLUGIN_NAME);
  }

  private CursedPluginInstanceRetrieverOnlyForUtilityClassesProvider() {
    throw new UnsupportedOperationException("This is a utility class and cannot be instantiated");
  }

  public static Plugin retrievePluginInstance() {
    return PLUGIN_INSTANCE;
  }
}
