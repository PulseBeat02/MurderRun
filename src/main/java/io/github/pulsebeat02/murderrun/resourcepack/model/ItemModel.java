package io.github.pulsebeat02.murderrun.resourcepack.model;

import static net.kyori.adventure.key.Key.key;

import net.kyori.adventure.key.Key;
import team.unnamed.creative.model.Model;
import team.unnamed.creative.model.ModelTexture;
import team.unnamed.creative.model.ModelTextures;

public final class ItemModel {

  private static final Key GENERATED_MODEL_KEY = key("item/generated");

  private final ItemTexture texture;
  private final int id;

  public ItemModel(final ItemTexture texture, final int id) {
    this.texture = texture;
    this.id = id;
  }

  public Model stitchModel() {
    final Key modelKey = this.texture.getKeyWithoutExtension();
    final ModelTexture modelTexture = ModelTexture.ofKey(modelKey);
    final ModelTextures textures = ModelTextures.builder().addLayer(modelTexture).build();
    return Model.model()
        .parent(GENERATED_MODEL_KEY)
        .textures(textures)
        .key(modelKey)
        .build();
  }

  public ItemTexture getTexture() {
    return this.texture;
  }

  public int getId() {
    return this.id;
  }
}
