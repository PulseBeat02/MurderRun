package io.github.pulsebeat02.murderrun.resourcepack;

import io.github.pulsebeat02.murderrun.utils.ResourceUtils;
import net.kyori.adventure.key.Key;
import team.unnamed.creative.base.Writable;
import team.unnamed.creative.sound.Sound;
import team.unnamed.creative.texture.Texture;

import java.io.IOException;
import java.io.InputStream;

public class CustomSound {

  private final Key key;
  private final Writable data;

  public CustomSound(final String namespace) throws IOException {
    this.key = Key.key("murder_run", namespace);
    this.data = this.getSoundStream(namespace);
  }

  private Writable getSoundStream(final String namespace) throws IOException {
    final String path = String.format("assets/sounds/%s.ogg", namespace);
    final InputStream stream = ResourceUtils.getResourceAsStream(path);
    return Writable.copyInputStream(stream);
  }

  public Sound build() {
    return Sound.sound(this.key, this.data);
  }
}
