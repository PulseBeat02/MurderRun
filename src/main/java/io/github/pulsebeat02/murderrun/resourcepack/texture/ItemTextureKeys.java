package io.github.pulsebeat02.murderrun.resourcepack.texture;

import org.checkerframework.checker.initialization.qual.UnderInitialization;
import team.unnamed.creative.texture.Texture;

public enum ItemTextureKeys {
  CAR_PART_1("car_part_1.png", 1),
  CAR_PART_2("car_part_2.png", 2),
  CAR_PART_3("car_part_3.png", 3),
  CAR_PART_4("car_part_4.png", 4),
  CAR_PART_5("car_part_5.png", 5);

  private final Texture texture;
  private final int customModelDataId;

  ItemTextureKeys(final String name, final int customModelDataId) {
    this.texture = this.loadModel(name);
    this.customModelDataId = customModelDataId;
  }

  private Texture loadModel(@UnderInitialization ItemTextureKeys this, final String name) {
    final ResourcePackTexture tex = new ResourcePackTexture(name);
    return tex.build();
  }

  public Texture getTexture() {
    return this.texture;
  }

  public int getCustomModelDataId() {
    return this.customModelDataId;
  }
}
