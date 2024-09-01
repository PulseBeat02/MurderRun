package io.github.pulsebeat02.murderrun.game.gadget;

import static java.util.Objects.requireNonNull;

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

  private static final String GADGETS_PROPERTIES = "settings/game.properties";

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

  private ResourceBundle loadGadgetProperties(
      @UnderInitialization GadgetDataBundle this, final Path resourcePath) {
    try {
      this.checkExistence(resourcePath);
      try (final InputStream in = Files.newInputStream(resourcePath);
          final FastBufferedInputStream fast = new FastBufferedInputStream(in)) {
        return new PropertyResourceBundle(fast);
      }
    } catch (final IOException e) {
      throw new AssertionError(e);
    }
  }

  private void checkExistence(@UnderInitialization GadgetDataBundle this, final Path resourcePath)
      throws IOException {
    if (IOUtils.createFile(resourcePath)) {
      try (final InputStream in = IOUtils.getResourceAsStream(GADGETS_PROPERTIES)) {
        Files.copy(in, resourcePath, StandardCopyOption.REPLACE_EXISTING);
      }
    }
  }
}
