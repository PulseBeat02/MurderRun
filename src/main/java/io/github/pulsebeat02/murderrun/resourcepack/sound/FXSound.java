package io.github.pulsebeat02.murderrun.resourcepack.sound;

import team.unnamed.creative.sound.Sound;

import java.io.IOException;

public enum FXSound {
  ROUND_START("round_start");

  private final Sound sound;

  FXSound(final String name) {
    this.sound = this.loadSound(name);
  }

  private Sound loadSound(final String name) {
    try {
      return new CustomSound(name).build();
    } catch (final IOException e) {
      throw new AssertionError(String.format("Failed to load sound %s.ogg!", name), e);
    }
  }

  public Sound getSound() {
    return this.sound;
  }
}
