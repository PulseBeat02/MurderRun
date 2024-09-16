package io.github.pulsebeat02.murderrun.game;

import java.util.function.BooleanSupplier;
import org.bukkit.Bukkit;
import org.bukkit.Server;
import org.bukkit.plugin.PluginManager;

public enum Capabilities {
  LIB_DISGUISES(() -> {
    final Server server = Bukkit.getServer();
    final PluginManager manager = server.getPluginManager();
    return manager.isPluginEnabled("LibsDisguises");
  }),

  PLACEHOLDER_API(() -> {
    final Server server = Bukkit.getServer();
    final PluginManager manager = server.getPluginManager();
    return manager.isPluginEnabled("PlaceholderAPI");
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
