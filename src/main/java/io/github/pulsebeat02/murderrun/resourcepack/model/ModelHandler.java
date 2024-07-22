package io.github.pulsebeat02.murderrun.resourcepack.model;

import io.github.pulsebeat02.murderrun.resourcepack.texture.ItemTexture;
import net.kyori.adventure.key.Key;
import team.unnamed.creative.model.*;
import team.unnamed.creative.texture.Texture;

import java.util.ArrayList;
import java.util.List;

public final class ModelHandler {

  public ModelHandler() {}

  public Model diamondModel() {
    final List<ItemOverride> list = new ArrayList<>();
    final ItemTexture[] textures = ItemTexture.values();
    for (final ItemTexture texture : textures) {
      final int id = texture.getCustomModelDataId();
      final Texture tex = texture.getTexture();
      final Key key = tex.key();
      final ItemOverride override = ItemOverride.of(key, ItemPredicate.customModelData(id));
      list.add(override);
    }
    return Model.model()
        .key(Key.key("item/diamond"))
        .parent(Key.key("item/generated"))
        .overrides(list)
        .build();
  }
}
