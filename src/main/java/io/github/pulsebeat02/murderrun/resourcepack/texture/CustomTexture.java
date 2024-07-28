package io.github.pulsebeat02.murderrun.resourcepack.texture;

import io.github.pulsebeat02.murderrun.utils.ResourceUtils;
import net.kyori.adventure.key.Key;
import org.checkerframework.checker.initialization.qual.UnderInitialization;
import team.unnamed.creative.base.Writable;
import team.unnamed.creative.texture.Texture;

import java.io.IOException;
import java.io.InputStream;

public final class CustomTexture {

  private final Key key;
  private final Writable data;

  public CustomTexture(final String namespace) {
    this.key = Key.key("murder_run", namespace);
    this.data = this.getTextureStream(namespace);
  }

  private Writable getTextureStream(@UnderInitialization CustomTexture this, final String namespace) {
    final String path = String.format("assets/textures/%s.png", namespace);
        try (final InputStream stream = ResourceUtils.getResourceAsStream(path)) {
              return Writable.copyInputStream(stream);
    } catch (final IOException e) {
            throw new AssertionError(e);
        }
  }

  public Key getKey() {
    return this.key;
  }

  public Writable getData() {
    return this.data;
  }

  public Texture build() {
    return Texture.texture().key(this.key).data(this.data).build();
  }
}
