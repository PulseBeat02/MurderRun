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

import net.kyori.adventure.key.Key;
import team.unnamed.creative.model.Model;
import team.unnamed.creative.model.ModelTexture;
import team.unnamed.creative.model.ModelTextures;

public final class ItemModel {

  private final ItemTexture texture;
  private final Key parent;
  private final int id;

  public ItemModel(final ItemTexture texture, final int id) {
    this(texture, Model.ITEM_GENERATED, id);
  }

  public ItemModel(final ItemTexture texture, final Key parent, final int id) {
    this.texture = texture;
    this.parent = parent;
    this.id = id;
  }

  public Model stitchModel() {
    final Key modelKey = this.texture.getKeyWithoutExtension();
    final ModelTexture modelTexture = ModelTexture.ofKey(modelKey);
    final ModelTextures textures = ModelTextures.builder().addLayer(modelTexture).build();
    return Model.model().parent(this.parent).textures(textures).key(modelKey).build();
  }

  public ItemTexture getTexture() {
    return this.texture;
  }

  public int getId() {
    return this.id;
  }
}
