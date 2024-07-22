package io.github.pulsebeat02.murderrun.resourcepack;

import team.unnamed.creative.texture.Texture;

import java.io.IOException;

public enum ItemTexture {
  CAR_PART_1("car_part_1");

  private final Texture texture;

  ItemTexture(final String name) {
    this.texture = this.loadTexture(name);
  }

  private Texture loadTexture(final String name) {
    try {
      return new CustomTexture(name).build();
    } catch (final IOException e) {
      throw new AssertionError(String.format("Failed to load texture %s.png!", name), e);
    }
  }
}
