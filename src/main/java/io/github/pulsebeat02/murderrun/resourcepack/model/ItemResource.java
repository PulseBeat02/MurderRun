package io.github.pulsebeat02.murderrun.resourcepack.model;

import static net.kyori.adventure.key.Key.key;

import java.util.ArrayList;
import java.util.List;
import net.kyori.adventure.key.Key;
import team.unnamed.creative.model.ItemOverride;
import team.unnamed.creative.model.ItemPredicate;
import team.unnamed.creative.model.Model;
import team.unnamed.creative.model.ModelTexture;
import team.unnamed.creative.model.ModelTextures;

public final class ItemResource {

  private final ItemModel[] models;
  private final Key parent;
  private final Key key;

  public ItemResource(final String key, final ItemModel[] models) {
    this(key, Model.ITEM_GENERATED, models);
  }

  public ItemResource(final String key, final Key parent, final ItemModel[] models) {
    this.models = models;
    this.parent = parent;
    this.key = key(key);
  }

  public Model createVanillaModel() {
    final List<ItemOverride> overrides = this.createItemOverrides();
    final ModelTexture texture = ModelTexture.ofKey(this.key);
    final ModelTextures textures = ModelTextures.builder().addLayer(texture).build();
    return Model.model().key(this.key).parent(this.parent).overrides(overrides).textures(textures).build();
  }

  private List<ItemOverride> createItemOverrides() {
    final List<ItemOverride> overrides = new ArrayList<>();
    for (final ItemModel model : this.models) {
      final Model stitched = model.stitchModel();
      final Key key = stitched.key();
      final int id = model.getId();
      final ItemPredicate predicate = ItemPredicate.customModelData(id);
      final ItemOverride override = ItemOverride.of(key, predicate);
      overrides.add(override);
    }
    return overrides;
  }
}
