package io.github.pulsebeat02.murderrun.game;

import java.util.function.BooleanSupplier;
import org.bukkit.Bukkit;
import org.bukkit.Server;
import org.bukkit.plugin.PluginManager;

public enum Capabilities {
  LIBDISG(() -> {
    final Server server = Bukkit.getServer();
    final PluginManager manager = server.getPluginManager();
    return manager.isPluginEnabled("LibsDisguises");
  }),

  PAPI(() -> {
    final Server server = Bukkit.getServer();
    final PluginManager manager = server.getPluginManager();
    return manager.isPluginEnabled("PlaceholderAPI");
  }),

  FAWE(() -> {
    try {
      Class.forName("com.fastasyncworldedit.bukkit.FaweBukkit");
      return true;
    } catch (ClassNotFoundException e) {
      return false;
    }
  });

  private final boolean enabled;

  Capabilities(final BooleanSupplier check) {
    this.enabled = check.getAsBoolean();
  }

  public boolean isEnabled() {
    return this.enabled;
  }

  public boolean isDisabled() {
    return !this.enabled;
  }
}
