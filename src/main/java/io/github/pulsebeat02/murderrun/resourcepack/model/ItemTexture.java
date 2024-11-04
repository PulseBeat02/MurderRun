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

import io.github.pulsebeat02.murderrun.immutable.Keys;
import io.github.pulsebeat02.murderrun.utils.IOUtils;
import net.kyori.adventure.key.Key;
import team.unnamed.creative.base.Writable;
import team.unnamed.creative.texture.Texture;

public final class ItemTexture {

  private static final String PLUGIN_TEXTURE_RESOURCE = "textures/%s.png";

  private final Key keyWithExtension;
  private final Key keyWithoutExtension;
  private final String name;
  private final String path;

  // resource without .png extension
  public ItemTexture(final String name) {
    final String path = "item/%s".formatted(name);
    this.name = name;
    this.keyWithExtension = key(Keys.NAMESPACE, path + ".png");
    this.keyWithoutExtension = key(Keys.NAMESPACE, path);
    this.path = PLUGIN_TEXTURE_RESOURCE.formatted(name);
  }

  public Texture stitchTexture() {
    final Writable writable = IOUtils.getWritableStreamFromResource(this.path);
    return Texture.texture().key(this.keyWithExtension).data(writable).build();
  }

  public Key getKeyWithExtension() {
    return this.keyWithExtension;
  }

  public Key getKeyWithoutExtension() {
    return this.keyWithoutExtension;
  }

  public String getName() {
    return this.name;
  }

  public String getPath() {
    return this.path;
  }
}
