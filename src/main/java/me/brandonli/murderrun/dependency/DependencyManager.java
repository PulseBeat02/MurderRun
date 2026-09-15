/*
 * This file is part of Murder Run, a spin-off game-mode of Dead by Daylight
 * Copyright (C) Brandon Li <https://brandonli.me/>
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program.  If not, see <https://www.gnu.org/licenses/>.
 */
package me.brandonli.murderrun.dependency;

import java.io.File;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.Collection;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import me.brandonli.murderrun.MurderRun;
import org.bukkit.Server;
import org.bukkit.plugin.InvalidDescriptionException;
import org.bukkit.plugin.InvalidPluginException;
import org.bukkit.plugin.Plugin;
import org.bukkit.plugin.PluginManager;
import org.bukkit.plugin.UnknownDependencyException;
import org.checkerframework.checker.nullness.qual.Nullable;
import org.slf4j.Logger;

public final class DependencyManager {

  private final MurderRun plugin;
  private final Logger logger;
  private final PluginJarScanner scanner;
  private final DependencyDownloader downloader;
  private final Map<Plugin, Path> installed;

  public DependencyManager(final MurderRun plugin) {
    this.plugin = plugin;
    this.logger = plugin.getSLF4JLogger();
    this.scanner = new PluginJarScanner();
    this.downloader = new DependencyDownloader(this.scanner, this.logger);
    this.installed = new LinkedHashMap<>();
  }

  public void installDependencies() {
    final Server server = this.plugin.getServer();
    final PluginManager manager = server.getPluginManager();
    final List<Dependency> missing = this.findUninstalledDependencies(manager);
    if (missing.isEmpty()) {
      return;
    }
    final String minecraftVersion = server.getMinecraftVersion();
    final Path folder = this.getPluginsFolder();
    try (final DependencyClient client = new HttpDependencyClient()) {
      for (final Dependency dependency : missing) {
        this.installDependency(client, manager, dependency, folder, minecraftVersion);
      }
    }
  }

  private List<Dependency> findUninstalledDependencies(final PluginManager manager) {
    final List<Dependency> dependencies = DependencyListing.getDependencies();
    final List<Dependency> missing = new ArrayList<>();
    for (final Dependency dependency : dependencies) {
      final String name = dependency.getName();
      if (manager.getPlugin(name) == null) {
        missing.add(dependency);
      }
    }
    return missing;
  }

  private void installDependency(
      final DependencyClient client,
      final PluginManager manager,
      final Dependency dependency,
      final Path folder,
      final String minecraftVersion) {
    final String name = dependency.getName();
    try {
      final Optional<Path> existing = this.scanner.findPluginJar(folder, name);
      if (existing.isPresent()) {
        final String msg =
            "Found {} for required plugin {}, but the server did not load it. Not downloading another copy, check the log above for why it failed to load.";
        this.logger.warn(msg, existing.get(), name);
        return;
      }
      this.logger.info(
          "Required plugin {} is missing, downloading it for Minecraft {}", name, minecraftVersion);
      final DependencyArtifact artifact = dependency.resolve(client, minecraftVersion);
      final Path jar = this.downloader.download(client, artifact, folder, name);
      final String fileName = artifact.getFileName();
      this.logger.info("Downloaded {} to {}", fileName, jar);
      final Plugin loaded = this.loadPlugin(manager, jar);
      this.installed.put(loaded, jar);
    } catch (final RuntimeException e) {
      final String msg = "Failed to automatically install required plugin %s".formatted(name);
      this.logger.error(msg, name, e);
    }
  }

  public void enableDependencies() {
    final Server server = this.plugin.getServer();
    final PluginManager manager = server.getPluginManager();
    final List<String> enabled = new ArrayList<>();
    for (final Map.Entry<Plugin, Path> entry : this.installed.entrySet()) {
      final Plugin dependency = entry.getKey();
      final Path jar = entry.getValue();
      if (!dependency.isEnabled()) {
        manager.enablePlugin(dependency);
      }
      if (!dependency.isEnabled()) {
        this.removeFailedDependency(dependency, jar);
        continue;
      }
      final String name = dependency.getName();
      enabled.add(name);
    }
    if (enabled.isEmpty()) {
      return;
    }
    final String msg =
        "Installed {} while the server was starting. If any other plugins depend on them, restart the server so those plugins load correctly.";
    this.logger.warn(msg, enabled);
  }

  private void removeFailedDependency(final Plugin dependency, final Path jar) {
    final String name = dependency.getName();
    final File file = jar.toFile();
    file.deleteOnExit();
    final String msg =
        "{} failed to enable, so the downloaded {} will be removed when the server stops. The server will try to enable it once more and log a second error, which can be ignored. Install a version that supports this server manually.";
    this.logger.warn(msg, name, jar);
  }

  public Collection<String> getMissingDependencies() {
    final Server server = this.plugin.getServer();
    final PluginManager manager = server.getPluginManager();
    final List<Dependency> dependencies = DependencyListing.getDependencies();
    final List<String> missing = new ArrayList<>();
    for (final Dependency dependency : dependencies) {
      final String name = dependency.getName();
      final @Nullable Plugin target = manager.getPlugin(name);
      if (target == null) {
        missing.add("%s (not installed)".formatted(name));
      } else if (!target.isEnabled()) {
        missing.add("%s (installed but failed to enable)".formatted(name));
      }
    }
    return missing;
  }

  private Plugin loadPlugin(final PluginManager manager, final Path jar) {
    final File file = jar.toFile();
    try {
      final @Nullable Plugin loaded = manager.loadPlugin(file);
      if (loaded == null) {
        final String message = "Server refused to load %s".formatted(jar);
        throw new DependencyException(message);
      }
      return loaded;
    } catch (final InvalidPluginException
        | InvalidDescriptionException
        | UnknownDependencyException e) {
      final String message = "Failed to load %s".formatted(jar);
      throw new DependencyException(message, e);
    }
  }

  private Path getPluginsFolder() {
    final File data = this.plugin.getDataFolder();
    final File plugins = data.getAbsoluteFile().getParentFile();
    if (plugins == null) {
      final String message = "Unable to locate the plugins folder from %s".formatted(data);
      throw new DependencyException(message);
    }
    return plugins.toPath();
  }
}
