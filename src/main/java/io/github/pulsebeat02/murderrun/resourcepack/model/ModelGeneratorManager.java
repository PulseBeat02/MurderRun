package io.github.pulsebeat02.murderrun.resourcepack.model;

import static net.kyori.adventure.key.Key.key;

import io.github.pulsebeat02.murderrun.resourcepack.texture.ItemTextureKeys;
import io.github.pulsebeat02.murderrun.resourcepack.texture.ResourcePackTexture;
import java.util.ArrayList;
import java.util.List;
import net.kyori.adventure.key.Key;
import team.unnamed.creative.model.ItemOverride;
import team.unnamed.creative.model.ItemPredicate;
import team.unnamed.creative.model.Model;
import team.unnamed.creative.texture.Texture;

public final class ModelGeneratorManager {

  private static final String DIAMOND_ITEM_MODEL = "item/diamond";
  private static final String DIAMOND_SWORD_ITEM_MODEL = "item/diamond_sword";
  private static final String GENERATED_ITEM_MODEL = "item/generated";
  private static final String SWORD_RESOURCE_PATH = "sword";
  private static final String PUMPKIN_BLUR_RESOURCE_PATH = "pumpkinblur";
  private static final String PUMPKIN_BLUR_NAMESPACE = "pumpkinblur";

  public ModelGeneratorManager() {}

  public Model customItemModelGenerator() {
    final List<ItemOverride> list = new ArrayList<>();
    final ItemTextureKeys[] textures = ItemTextureKeys.values();
    for (final ItemTextureKeys texture : textures) {
      final int id = texture.getCustomModelDataId();
      final Texture tex = texture.getTexture();
      final Key key = tex.key();
      final ItemPredicate predicate = ItemPredicate.customModelData(id);
      final ItemOverride override = ItemOverride.of(key, predicate);
      list.add(override);
    }
    return Model.model()
        .key(key(DIAMOND_ITEM_MODEL))
        .parent(key(GENERATED_ITEM_MODEL))
        .overrides(list)
        .build();
  }

  public Model customSwordGenerator() {
    final Texture texture = new ResourcePackTexture(SWORD_RESOURCE_PATH).build();
    final ItemPredicate predicate = ItemPredicate.customModelData(1);
    final Key key = texture.key();
    final ItemOverride override = ItemOverride.of(key, predicate);
    final List<ItemOverride> list = List.of(override);
    return Model.model()
        .key(key(DIAMOND_SWORD_ITEM_MODEL))
        .parent(key(GENERATED_ITEM_MODEL))
        .overrides(list)
        .build();
  }

  public Texture customJumpScareGenerator() {
    final String namespace = Key.MINECRAFT_NAMESPACE;
    final Key key = key(namespace, PUMPKIN_BLUR_RESOURCE_PATH);
    final ResourcePackTexture texture = new ResourcePackTexture(key, PUMPKIN_BLUR_NAMESPACE);
    return texture.build();
  }
}
