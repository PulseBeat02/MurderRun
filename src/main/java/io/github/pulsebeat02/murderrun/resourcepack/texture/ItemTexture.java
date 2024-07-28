package io.github.pulsebeat02.murderrun.resourcepack.texture;

import org.checkerframework.checker.initialization.qual.UnderInitialization;
import team.unnamed.creative.texture.Texture;

public enum ItemTexture {
  CAR_PART_1("car_part_1", 1),
  CAR_PART_2("car_part_2", 2),
  CAR_PART_3("car_part_3", 3),
  CAR_PART_4("car_part_4", 4),
  CAR_PART_5("car_part_5", 5);

  private final Texture texture;
  private final int customModelDataId;

  ItemTexture(final String name, final int customModelDataId) {
    this.texture = this.loadModel(name);
    this.customModelDataId = customModelDataId;
  }

  private Texture loadModel(@UnderInitialization ItemTexture this, final String name) {
    final CustomTexture tex = new CustomTexture(name);
    return tex.build();
  }

  public Texture getTexture() {
    return this.texture;
  }

  public int getCustomModelDataId() {
    return this.customModelDataId;
  }
}
