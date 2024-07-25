package io.github.pulsebeat02.murderrun.resourcepack.texture;

import io.github.pulsebeat02.murderrun.utils.ResourceUtils;
import java.io.IOException;
import java.io.InputStream;
import net.kyori.adventure.key.Key;
import team.unnamed.creative.base.Writable;
import team.unnamed.creative.texture.Texture;

public final class CustomTexture {

  private final Key key;
  private final Writable data;

  public CustomTexture(final String namespace) throws IOException {
    this.key = Key.key("murder_run", namespace);
    this.data = this.getTextureStream(namespace);
  }

  private Writable getTextureStream(final String namespace) throws IOException {
    final String path = String.format("assets/textures/%s.png", namespace);
    final InputStream stream = ResourceUtils.getResourceAsStream(path);
    return Writable.copyInputStream(stream);
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
