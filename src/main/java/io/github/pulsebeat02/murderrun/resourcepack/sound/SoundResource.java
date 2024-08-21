package io.github.pulsebeat02.murderrun.resourcepack.sound;

import net.kyori.adventure.key.Key;
import team.unnamed.creative.sound.Sound;
import team.unnamed.creative.sound.SoundEntry;
import team.unnamed.creative.sound.SoundEvent;

public final class SoundResource {

  private final SoundFile sound;
  private final Key key;

  public SoundResource(final SoundFile sound) {
    this.sound = sound;
    this.key = sound.getKey();
  }

  public SoundEvent stitchSound() {
    final Sound sound = this.sound.stitchSound();
    final SoundEntry entry = SoundEntry.soundEntry(sound);
    return SoundEvent.soundEvent().key(this.key).addSound(entry).build();
  }

  public SoundFile getSound() {
    return sound;
  }

  public Key getKey() {
    return key;
  }
}
