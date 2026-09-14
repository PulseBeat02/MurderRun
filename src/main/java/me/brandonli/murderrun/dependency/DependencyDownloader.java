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

import com.google.common.hash.HashCode;
import com.google.common.hash.HashFunction;
import com.google.common.hash.Hashing;
import java.io.IOException;
import java.net.URI;
import java.nio.file.AtomicMoveNotSupportedException;
import java.nio.file.Files;
import java.nio.file.InvalidPathException;
import java.nio.file.Path;
import java.nio.file.StandardCopyOption;

public final class DependencyDownloader {

  private static final String PARTIAL_SUFFIX = ".part";

  private final PluginJarScanner scanner;

  public DependencyDownloader(final PluginJarScanner scanner) {
    this.scanner = scanner;
  }

  public Path download(
      final DependencyClient client,
      final DependencyArtifact artifact,
      final Path folder,
      final String pluginName) {
    final String fileName = artifact.getFileName();
    this.checkFileName(fileName);
    final Path destination = folder.resolve(fileName);
    if (Files.exists(destination)) {
      final String message = "Refusing to overwrite existing file %s".formatted(destination);
      throw new DependencyException(message);
    }
    final Path partial = folder.resolve(fileName + PARTIAL_SUFFIX);
    try {
      Files.createDirectories(folder);
      Files.deleteIfExists(partial);
      final URI uri = artifact.getUri();
      client.download(uri, partial);
      this.verifyHash(artifact, partial);
      this.verifyPlugin(partial, fileName, pluginName);
      this.move(partial, destination);
      return destination;
    } catch (final IOException e) {
      final String message = "Failed to write %s".formatted(destination);
      throw new DependencyException(message, e);
    } finally {
      this.deleteQuietly(partial);
    }
  }

  private void checkFileName(final String fileName) {
    final Path path;
    try {
      path = Path.of(fileName);
    } catch (final InvalidPathException e) {
      final String message = "Refusing to download suspicious file name %s".formatted(fileName);
      throw new DependencyException(message, e);
    }
    final Path name = path.getFileName();
    if (name == null || !fileName.equals(name.toString()) || !fileName.endsWith(".jar")) {
      final String message = "Refusing to download suspicious file name %s".formatted(fileName);
      throw new DependencyException(message);
    }
  }

  private void verifyHash(final DependencyArtifact artifact, final Path path) throws IOException {
    final String expected = artifact.getSha512();
    if (expected == null) {
      return;
    }
    final HashFunction function = Hashing.sha512();
    final byte[] bytes = Files.readAllBytes(path);
    final HashCode code = function.hashBytes(bytes);
    final String actual = code.toString();
    if (!actual.equalsIgnoreCase(expected)) {
      final String fileName = artifact.getFileName();
      final String message =
          "Hash mismatch for %s (expected %s, got %s)".formatted(fileName, expected, actual);
      throw new DependencyException(message);
    }
  }

  private void verifyPlugin(final Path path, final String fileName, final String pluginName) {
    if (!this.scanner.declaresPlugin(path, pluginName)) {
      final String message = "%s is not a valid jar for plugin %s".formatted(fileName, pluginName);
      throw new DependencyException(message);
    }
  }

  private void move(final Path source, final Path destination) throws IOException {
    try {
      Files.move(source, destination, StandardCopyOption.ATOMIC_MOVE);
    } catch (final AtomicMoveNotSupportedException e) {
      Files.move(source, destination);
    }
  }

  private void deleteQuietly(final Path path) {
    try {
      Files.deleteIfExists(path);
    } catch (final IOException ignored) {
    }
  }
}
