package io.github.pulsebeat02.murderrun.resourcepack.texture;

import static net.kyori.adventure.key.Key.key;

import io.github.pulsebeat02.murderrun.immutable.Keys;
import io.github.pulsebeat02.murderrun.utils.ResourceUtils;
import java.io.IOException;
import java.io.InputStream;
import net.kyori.adventure.key.Key;
import org.checkerframework.checker.initialization.qual.UnderInitialization;
import team.unnamed.creative.base.Writable;
import team.unnamed.creative.texture.Texture;

public final class ResourcePackTexture {

  private static final String PATH_RESOURCE = "assets/textures/%s.png";

  private final Key key;
  private final Writable data;

  public ResourcePackTexture(final String namespace) {
    this(key(Keys.NAMESPACE, namespace), namespace);
  }

  public ResourcePackTexture(final Key key, final String namespace) {
    this.key = key;
    this.data = this.getTextureStream(namespace);
  }

  private Writable getTextureStream(
      @UnderInitialization ResourcePackTexture this, final String namespace) {
    final String path = PATH_RESOURCE.formatted(namespace);
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
    final Texture.Builder builder = Texture.texture().key(this.key).data(this.data);
    return builder.build();
  }
}
