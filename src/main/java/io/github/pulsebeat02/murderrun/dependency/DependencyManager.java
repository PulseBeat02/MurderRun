package io.github.pulsebeat02.murderrun.dependency;

import static java.util.Objects.requireNonNull;

import java.io.File;
import java.nio.file.Path;
import java.util.Collection;
import org.bukkit.Bukkit;
import org.bukkit.Server;
import org.bukkit.plugin.InvalidDescriptionException;
import org.bukkit.plugin.InvalidPluginException;
import org.bukkit.plugin.Plugin;
import org.bukkit.plugin.PluginManager;

public final class DependencyManager {

  public void installDependencies() {

    final DependencyListing listing = DependencyListing.getCurrentListing();
    if (listing == null) {
      throw new UnsupportedOperationException("The current server version isn't supported by this plugin!");
    }

    final Collection<Dependency> dependencies = listing.getDependencies();
    for (final Dependency dependency : dependencies) {
      if (!this.needsInstallation(dependency)) {
        continue;
      }
      final Path downloaded = dependency.download();
      this.loadPluginAtRuntime(downloaded);
    }
  }

  public void loadPluginAtRuntime(final Path file) {
    final Server server = Bukkit.getServer();
    final PluginManager manager = server.getPluginManager();
    final File legacy = file.toFile();
    try {
      final Plugin target = requireNonNull(manager.loadPlugin(legacy));
      target.onLoad();
      manager.enablePlugin(target);
    } catch (final InvalidDescriptionException | InvalidPluginException e) {
      throw new AssertionError(e);
    }
  }

  private boolean needsInstallation(final Dependency dependency) {
    final Server server = Bukkit.getServer();
    final PluginManager manager = server.getPluginManager();
    final String target = dependency.getName();
    return manager.getPlugin(target) == null;
  }
}
