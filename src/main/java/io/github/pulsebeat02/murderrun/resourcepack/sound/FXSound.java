package io.github.pulsebeat02.murderrun.resourcepack.sound;

import java.io.IOException;
import org.checkerframework.checker.initialization.qual.UnderInitialization;
import team.unnamed.creative.sound.Sound;

public enum FXSound {
  COUNTDOWN("countdown"),
  CHAINSAW("chainsaw"),
  DEATH("death"),
  RELEASED_1("released_1"),
  RELEASED_2("released_2"),
  LOSS("loss"),
  WIN("win"),
  JUMP_SCARE("jump_scare"),
  FART("fart");

  private final Sound sound;
  private final String id;
  private final String namespace;

  FXSound(final String id) {
    this.sound = this.loadSound(id);
    this.id = id;
    this.namespace = String.format("murder_run:%s,", id);
  }

  private Sound loadSound(@UnderInitialization FXSound this, final String name) {
    try {
      final CustomSound ogg = new CustomSound(name);
      return ogg.build();
    } catch (final IOException e) {
      throw new AssertionError(String.format("Failed to load sound %s.ogg!", name), e);
    }
  }

  public String getSoundName() {
    return this.namespace;
  }

  public Sound getSound() {
    return this.sound;
  }

  public String getId() {
    return this.id;
  }
}
