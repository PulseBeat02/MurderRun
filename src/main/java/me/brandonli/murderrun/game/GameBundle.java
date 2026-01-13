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
package me.brandonli.murderrun.game;

import static java.util.Objects.requireNonNull;

import it.unimi.dsi.fastutil.io.FastBufferedInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardCopyOption;
import java.util.PropertyResourceBundle;
import java.util.ResourceBundle;
import me.brandonli.murderrun.data.dfu.PropertyFixerManager;
import me.brandonli.murderrun.utils.IOUtils;
import org.checkerframework.checker.initialization.qual.UnderInitialization;

public final class GameBundle {

  private static final String GADGETS_PROPERTIES = "%s.game.properties";

  private final String file;
  private final ResourceBundle bundle;

  public GameBundle(final String name) {
    this.file = GADGETS_PROPERTIES.formatted(name);
    final Path pluginDataFolder = IOUtils.getPluginDataFolderPath();
    final Path resourcePath = pluginDataFolder.resolve(this.file);
    this.bundle = this.loadGadgetProperties(resourcePath);
  }

  public String getString(final String key) {
    requireNonNull(key);
    return this.bundle.getString(key);
  }

  public int getInt(final String key) {
    final String raw = requireNonNull(this.getString(key));
    return Integer.parseInt(raw);
  }

  public boolean getBoolean(final String key) {
    return Boolean.parseBoolean(this.getString(key));
  }

  public double getDouble(final String key) {
    final String raw = requireNonNull(this.getString(key));
    return Double.parseDouble(raw);
  }

  private ResourceBundle loadGadgetProperties(@UnderInitialization GameBundle this, final Path resourcePath) {
    try {
      this.checkExistence(resourcePath);
      try (
        final InputStream in = Files.newInputStream(resourcePath);
        final FastBufferedInputStream fast = new FastBufferedInputStream(in)
      ) {
        final ResourceBundle bundle = new PropertyResourceBundle(fast);
        final PropertyFixerManager fixer = new PropertyFixerManager();
        fixer.registerGamePropertiesFixer();
        fixer.applyFixersUpTo(bundle);
        return bundle;
      }
    } catch (final IOException e) {
      throw new AssertionError(e);
    }
  }

  private void checkExistence(@UnderInitialization GameBundle this, final Path resourcePath) throws IOException {
    if (IOUtils.createFile(resourcePath)) {
      final String file = requireNonNull(this.file);
      try (final InputStream in = IOUtils.getResourceAsStream(file)) {
        Files.copy(in, resourcePath, StandardCopyOption.REPLACE_EXISTING);
      }
    }
  }
}
