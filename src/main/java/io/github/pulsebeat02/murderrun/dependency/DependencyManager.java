package io.github.pulsebeat02.murderrun.dependency;

import static java.util.Objects.requireNonNull;

import io.github.pulsebeat02.murderrun.utils.IOUtils;
import java.io.File;
import java.nio.file.Path;
import org.bukkit.Bukkit;
import org.bukkit.Server;
import org.bukkit.plugin.InvalidDescriptionException;
import org.bukkit.plugin.InvalidPluginException;
import org.bukkit.plugin.Plugin;
import org.bukkit.plugin.PluginManager;

public final class DependencyManager {

  public void installDependencies() {

    final Path data = IOUtils.getPluginDataFolderPath();
    final Path parent = requireNonNull(data.getParent());
    final PluginDependency[] dependencies = {
      new JenkinsDependency(
          "Citizens", "Citizens-2.0.35-b3580", parent, "https://ci.citizensnpcs.co/job/Citizens2/"),
      new ModrinthDependency("FastAsyncWorldEdit", "2.11.2", parent)
    };

    for (final PluginDependency dependency : dependencies) {
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

  private boolean needsInstallation(final PluginDependency dependency) {
    final Server server = Bukkit.getServer();
    final PluginManager manager = server.getPluginManager();
    final String target = dependency.getName();
    return !manager.isPluginEnabled(target);
  }
}
