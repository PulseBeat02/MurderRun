package io.github.pulsebeat02.murderrun.dependency;

import static java.util.Objects.requireNonNull;

import java.io.File;
import java.nio.file.Path;
import java.util.Collection;
import java.util.Set;
import org.bukkit.Bukkit;
import org.bukkit.Server;
import org.bukkit.plugin.InvalidDescriptionException;
import org.bukkit.plugin.InvalidPluginException;
import org.bukkit.plugin.Plugin;
import org.bukkit.plugin.PluginManager;

public final class DependencyManager {

  private static final Collection<PluginDependency> PLUGIN_JAR_DEPENDENCIES = Set.of(
    new JenkinsDependency("Citizens", "Citizens-2.0.36-b3612", "https://ci.citizensnpcs.co/job/Citizens2/3612/artifact/dist/target/"),
    new JenkinsDependency(
      "WorldEdit",
      "FastAsyncWorldEdit-Bukkit-2.12.1-SNAPSHOT-952",
      "https://ci.athion.net/job/FastAsyncWorldEdit/952/artifact/artifacts/"
    )
  );

  public void installDependencies() {
    for (final PluginDependency dependency : PLUGIN_JAR_DEPENDENCIES) {
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
    return manager.getPlugin(target) == null;
  }
}
