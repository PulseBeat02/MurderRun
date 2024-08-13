package io.github.pulsebeat02.murderrun.resourcepack.sound;

import static net.kyori.adventure.key.Key.key;

import io.github.pulsebeat02.murderrun.immutable.Keys;
import io.github.pulsebeat02.murderrun.utils.ResourceUtils;
import java.io.IOException;
import java.io.InputStream;
import net.kyori.adventure.key.Key;
import org.checkerframework.checker.initialization.qual.UnderInitialization;
import team.unnamed.creative.base.Writable;
import team.unnamed.creative.sound.Sound;

public final class ResourcePackSound {

  private static final String PATH_RESOURCE = "assets/sounds/%s.ogg";

  private final Key key;
  private final Writable data;

  public ResourcePackSound(final String namespace) {
    this.key = key(Keys.NAMESPACE, namespace);
    this.data = this.getSoundStream(namespace);
  }

  private Writable getSoundStream(
      @UnderInitialization ResourcePackSound this, final String namespace) {
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

  public Sound build() {
    return Sound.sound(this.key, this.data);
  }
}
