package io.github.pulsebeat02.murderrun.resourcepack.model;

import io.github.pulsebeat02.murderrun.resourcepack.texture.CustomTexture;
import io.github.pulsebeat02.murderrun.resourcepack.texture.ItemTexture;

import java.util.ArrayList;
import java.util.List;
import net.kyori.adventure.key.Key;
import team.unnamed.creative.model.*;
import team.unnamed.creative.texture.Texture;

public final class ModelHandler {

  public ModelHandler() {}

  public Model customItemModelGenerator() {
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

  public Model customSwordGenerator() {
    final Texture texture = new CustomTexture("sword").build();
    final ItemPredicate predicate = ItemPredicate.customModelData(1);
    final Key key = texture.key();
    final ItemOverride override = ItemOverride.of(key, predicate);
    final List<ItemOverride> list = List.of(override);
    return Model.model()
        .key(Key.key("item/diamond_sword"))
        .parent(Key.key("item/generated"))
        .overrides(list)
        .build();
  }

  public Model customJumpScareGenerator() {
    final Texture texture = new CustomTexture("jump_scare").build();
    final ItemPredicate predicate = ItemPredicate.customModelData(1);
    final Key key = texture.key();
    final ItemOverride override = ItemOverride.of(key, predicate);
    final List<ItemOverride> list = List.of(override);
    return Model.model()
            .key(Key.key("item/carved_pumpkin"))
            .parent(Key.key("item/generated"))
            .overrides(list)
            .build();
  }
}
