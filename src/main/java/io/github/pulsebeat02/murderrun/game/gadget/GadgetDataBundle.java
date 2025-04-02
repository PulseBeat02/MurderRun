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
package io.github.pulsebeat02.murderrun.game.gadget;

import static java.util.Objects.requireNonNull;

import io.github.pulsebeat02.murderrun.dfu.PropertyFixerManager;
import io.github.pulsebeat02.murderrun.utils.IOUtils;
import it.unimi.dsi.fastutil.io.FastBufferedInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardCopyOption;
import java.util.PropertyResourceBundle;
import java.util.ResourceBundle;
import org.checkerframework.checker.initialization.qual.UnderInitialization;

public final class GadgetDataBundle {

  private static final String GADGETS_PROPERTIES = "game.properties";

  private final Path resourcePath;
  private final ResourceBundle bundle;

  public GadgetDataBundle() {
    final Path pluginDataFolder = IOUtils.getPluginDataFolderPath();
    this.resourcePath = pluginDataFolder.resolve(GADGETS_PROPERTIES);
    this.bundle = this.loadGadgetProperties(this.resourcePath);
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

  private ResourceBundle loadGadgetProperties(@UnderInitialization GadgetDataBundle this, final Path resourcePath) {
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

  private void checkExistence(@UnderInitialization GadgetDataBundle this, final Path resourcePath) throws IOException {
    if (IOUtils.createFile(resourcePath)) {
      try (final InputStream in = IOUtils.getResourceAsStream(GADGETS_PROPERTIES)) {
        Files.copy(in, resourcePath, StandardCopyOption.REPLACE_EXISTING);
      }
    }
  }
}
