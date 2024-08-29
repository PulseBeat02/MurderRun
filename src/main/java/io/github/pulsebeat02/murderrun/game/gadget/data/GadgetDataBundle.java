package io.github.pulsebeat02.murderrun.game.gadget.data;

import com.google.common.primitives.Doubles;
import com.google.common.primitives.Ints;
import io.github.pulsebeat02.murderrun.utils.IOUtils;
import it.unimi.dsi.fastutil.io.FastBufferedInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.PropertyResourceBundle;
import java.util.ResourceBundle;

public final class GadgetDataBundle {

  private static final String GADGETS_PROPERTIES = "settings/gadgets.properties";

  private final Path resourcePath;
  private final ResourceBundle bundle;

  public GadgetDataBundle() {
    final Path pluginDataFolder = IOUtils.getPluginDataFolderPath();
    this.resourcePath = pluginDataFolder.resolve(GADGETS_PROPERTIES);
    this.bundle = this.loadGadgetProperties();
  }

  public String getString(final String key) {
    return this.bundle.getString(key);
  }

  public Integer getInt(final String key) {
    return Ints.tryParse(this.getString(key));
  }

  public boolean getBoolean(final String key) {
    return Boolean.parseBoolean(this.getString(key));
  }

  public Double getDouble(final String key) {
    return Doubles.tryParse(this.getString(key));
  }

  private ResourceBundle loadGadgetProperties() {
    try {
      this.checkExistance();
      try (final InputStream in = Files.newInputStream(this.resourcePath);
          final FastBufferedInputStream fast = new FastBufferedInputStream(in)) {
        return new PropertyResourceBundle(fast);
      }
    } catch (final IOException e) {
      throw new AssertionError(e);
    }
  }

  private void checkExistance() throws IOException {
    IOUtils.createFolder(this.resourcePath);
    if (Files.notExists(this.resourcePath)) {
      try (final InputStream in = IOUtils.getResourceAsStream(GADGETS_PROPERTIES)) {
        Files.copy(in, this.resourcePath);
      }
    }
  }
}
