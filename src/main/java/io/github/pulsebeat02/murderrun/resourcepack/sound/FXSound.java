package io.github.pulsebeat02.murderrun.resourcepack.sound;

import team.unnamed.creative.sound.Sound;

import java.io.IOException;

public enum FXSound {
  COUNTDOWN("countdown"),
  CHAINSAW("chainsaw"),
  DEATH_1("death_1"),
  DEATH_2("death_2"),
  DEATH_3("death_3"),
  RELEASED_1("released_1"),
  RELEASED_2("released_2");

  private final Sound sound;
  private final String id;

  FXSound(final String id) {
    this.sound = this.loadSound(id);
    this.id = id;
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

  public String getId() {
    return this.id;
  }
}
