/*

MIT License

Copyright (c) 2024 Brandon Li

Permission is hereby granted, free of charge, to any person obtaining a copy
of this software and associated documentation files (the "Software"), to deal
in the Software without restriction, including without limitation the rights
to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
copies of the Software, and to permit persons to whom the Software is
furnished to do so, subject to the following conditions:

The above copyright notice and this permission notice shall be included in all
copies or substantial portions of the Software.

THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE
SOFTWARE.

*/
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
