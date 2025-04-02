/*

MIT License

Copyright (c) 2024 Brandon Li

Permission is hereby granted, free of charge, to any person obtaining a copy
of this software and associated documentation files (the "Software"), to deal
in the Software without restriction, including without limitation the rights
to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
copies of the Software, and to permit persons to whom the Software is
furnished to do so, subject to the following conditions:

The above copyright notice and this permission notice shall be included in all
copies or substantial portions of the Software.

THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE
SOFTWARE.

*/
package me.brandonli.murderrun.dependency;

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
