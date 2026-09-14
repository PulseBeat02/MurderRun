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

import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.Reader;
import java.nio.charset.StandardCharsets;
import java.nio.file.DirectoryStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.List;
import java.util.Locale;
import java.util.Optional;
import java.util.jar.JarFile;
import java.util.zip.ZipEntry;
import org.bukkit.configuration.InvalidConfigurationException;
import org.bukkit.configuration.file.YamlConfiguration;
import org.checkerframework.checker.nullness.qual.Nullable;

public final class PluginJarScanner {

  private static final List<String> DESCRIPTORS = List.of("paper-plugin.yml", "plugin.yml");

  public Optional<Path> findPluginJar(final Path folder, final String name) {
    if (!Files.isDirectory(folder)) {
      return Optional.empty();
    }
    try (final DirectoryStream<Path> stream = Files.newDirectoryStream(folder, "*.jar")) {
      for (final Path jar : stream) {
        if (this.declaresPlugin(jar, name)) {
          return Optional.of(jar);
        }
      }
    } catch (final IOException e) {
      final String message = "Failed to scan %s".formatted(folder);
      throw new DependencyException(message, e);
    }
    return Optional.empty();
  }

  public boolean declaresPlugin(final Path jar, final String name) {
    final String target = this.normalize(name);
    try (final JarFile file = new JarFile(jar.toFile())) {
      for (final String descriptor : DESCRIPTORS) {
        final @Nullable ZipEntry entry = file.getEntry(descriptor);
        if (entry == null) {
          continue;
        }
        final YamlConfiguration yaml = this.readDescriptor(file, entry);
        if (this.matches(yaml, target)) {
          return true;
        }
      }
    } catch (final IOException | InvalidConfigurationException e) {
      return false;
    }
    return false;
  }

  private YamlConfiguration readDescriptor(final JarFile file, final ZipEntry entry)
      throws IOException, InvalidConfigurationException {
    try (final InputStream stream = file.getInputStream(entry);
        final Reader reader = new InputStreamReader(stream, StandardCharsets.UTF_8)) {
      final YamlConfiguration yaml = new YamlConfiguration();
      yaml.load(reader);
      return yaml;
    }
  }

  private boolean matches(final YamlConfiguration yaml, final String target) {
    final @Nullable String declared = yaml.getString("name");
    if (declared != null && this.normalize(declared).equals(target)) {
      return true;
    }
    final List<String> provides = yaml.getStringList("provides");
    for (final String provided : provides) {
      if (this.normalize(provided).equals(target)) {
        return true;
      }
    }
    return false;
  }

  private String normalize(final String name) {
    final String replaced = name.replace(' ', '_');
    return replaced.toLowerCase(Locale.ENGLISH);
  }
}
