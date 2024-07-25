package io.github.pulsebeat02.murderrun.resourcepack.texture;

import java.io.IOException;
import team.unnamed.creative.texture.Texture;

public enum WeaponTexture {
  SWORD("sword", 1);

  private final Texture texture;
  private final int customModelDataId;

  WeaponTexture(final String name, final int customModelDataId) {
    this.texture = this.loadModel(name);
    this.customModelDataId = customModelDataId;
  }

  private Texture loadModel(final String name) {
    try {
      return new CustomTexture(name).build();
    } catch (final IOException e) {
      throw new AssertionError(String.format("Failed to load texture %s.png!", name), e);
    }
  }

  public Texture getTexture() {
    return this.texture;
  }

  public int getCustomModelDataId() {
    return this.customModelDataId;
  }
}
