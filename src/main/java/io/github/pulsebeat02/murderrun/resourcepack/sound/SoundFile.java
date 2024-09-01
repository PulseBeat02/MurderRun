package io.github.pulsebeat02.murderrun.resourcepack.sound;

import static net.kyori.adventure.key.Key.key;

import io.github.pulsebeat02.murderrun.immutable.Keys;
import io.github.pulsebeat02.murderrun.utils.IOUtils;
import net.kyori.adventure.key.Key;
import team.unnamed.creative.base.Writable;
import team.unnamed.creative.sound.Sound;

public final class SoundFile {

  private static final String PLUGIN_SOUND_RESOURCE = "sounds/%s.ogg";

  private final Key key;
  private final String name;
  private final String path;

  public SoundFile(final String name) {
    this.name = name;
    this.key = key(Keys.NAMESPACE, this.name);
    this.path = PLUGIN_SOUND_RESOURCE.formatted(name);
  }

  public Sound stitchSound() {
    final Writable writable = IOUtils.getWritableStreamFromResource(this.path);
    return Sound.sound(this.key, writable);
  }

  public Key getKey() {
    return this.key;
  }

  public String getName() {
    return this.name;
  }

  public String getPath() {
    return this.path;
  }
}
