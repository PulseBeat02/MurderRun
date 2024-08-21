package io.github.pulsebeat02.murderrun.resourcepack.model;

import static net.kyori.adventure.key.Key.key;

import io.github.pulsebeat02.murderrun.immutable.Keys;
import io.github.pulsebeat02.murderrun.utils.IOUtils;
import java.io.IOException;
import java.io.InputStream;
import net.kyori.adventure.key.Key;
import team.unnamed.creative.base.Writable;
import team.unnamed.creative.texture.Texture;

public final class ItemTexture {

  private static final String PLUGIN_TEXTURE_RESOURCE = "textures/%s.png";

  private final Key keyWithExtension;
  private final Key keyWithoutExtension;
  private final String name;
  private final String path;

  // resource without .png extension
  public ItemTexture(final String name) {
    this.name = name;
    this.keyWithExtension = key(Keys.NAMESPACE, "%s.png".formatted(name));
    this.keyWithoutExtension = key(Keys.NAMESPACE, name);
    this.path = PLUGIN_TEXTURE_RESOURCE.formatted(name);
  }

  public Texture stitchTexture() {
    final Writable writable = this.getWritableTexture();
    return Texture.texture().key(this.keyWithExtension).data(writable).build();
  }

  private Writable getWritableTexture() {
    try (final InputStream stream = IOUtils.getResourceAsStream(this.path)) {
      return Writable.copyInputStream(stream);
    } catch (final IOException e) {
      throw new AssertionError(e);
    }
  }

  public Key getKeyWithExtension() {
    return this.keyWithExtension;
  }

  public Key getKeyWithoutExtension() {
    return this.keyWithoutExtension;
  }

  public String getName() {
    return this.name;
  }

  public String getPath() {
    return this.path;
  }
}
