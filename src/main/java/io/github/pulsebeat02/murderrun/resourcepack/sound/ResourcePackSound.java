package io.github.pulsebeat02.murderrun.resourcepack.sound;

import static net.kyori.adventure.key.Key.key;

import io.github.pulsebeat02.murderrun.utils.ResourceUtils;
import java.io.IOException;
import java.io.InputStream;
import net.kyori.adventure.key.Key;
import org.checkerframework.checker.initialization.qual.UnderInitialization;
import team.unnamed.creative.base.Writable;
import team.unnamed.creative.sound.Sound;

public class ResourcePackSound {

  private final Key key;
  private final Writable data;

  public ResourcePackSound(final String namespace) throws IOException {
    this.key = key("murder_run", namespace);
    this.data = this.getSoundStream(namespace);
  }

  private Writable getSoundStream(
      @UnderInitialization ResourcePackSound this, final String namespace) throws IOException {
    final String path = String.format("assets/sounds/%s.ogg", namespace);
    final InputStream stream = ResourceUtils.getResourceAsStream(path);
    return Writable.copyInputStream(stream);
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
