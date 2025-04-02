/*

MIT License

Copyright (c) 2025 Brandon Li

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
package me.brandonli;

import static java.util.Objects.requireNonNull;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.List;
import java.util.stream.Stream;
import net.kyori.adventure.key.Key;
import org.intellij.lang.annotations.Subst;
import team.unnamed.creative.ResourcePack;
import team.unnamed.creative.base.Writable;
import team.unnamed.creative.model.Model;
import team.unnamed.creative.model.ModelTexture;
import team.unnamed.creative.model.ModelTextures;
import team.unnamed.creative.serialize.minecraft.MinecraftResourcePackWriter;
import team.unnamed.creative.texture.Texture;

public final class CreativeTest {

  public static void main(final String[] args) {
    final Path path = Path.of("resources").toAbsolutePath();
    try (final Stream<Path> paths = Files.walk(path)) {
      final List<Path> files = paths.filter(Files::isRegularFile).toList();
      final ResourcePack pack = ResourcePack.resourcePack();
      for (final Path file : files) {
        final Path fileName = requireNonNull(file.getFileName());
        @Subst("")
        final String name = fileName.toString();
        final String extension = name.substring(name.lastIndexOf('.') + 1);
        @Subst("")
        final String nameWithoutExtension = name.substring(0, name.lastIndexOf('.'));
        if (extension.equals("png") && !name.startsWith("diamond_layer") && !name.startsWith("bow_pulling")) {
          final Texture texture = Texture.texture().key(Key.key("murderrun", "item/" + name)).data(Writable.path(file)).build();
          final ModelTextures textures = ModelTextures.builder()
            .layers(ModelTexture.ofKey(Key.key("murderrun", "item/" + nameWithoutExtension)))
            .build();
          final Model model = Model.model()
            .parent(Model.ITEM_HANDHELD)
            .key(Key.key("murderrun", "item/" + nameWithoutExtension))
            .textures(textures)
            .build();
          pack.unknownFile(
            "assets/murderrun/items/" + nameWithoutExtension + ".json",
            Writable.stringUtf8(
              """
              {
                "model": {
                  "type": "minecraft:model",
                  "model": "murderrun:item/%s"
                }
              }""".formatted(nameWithoutExtension)
            )
          );
          pack.model(model);
          pack.texture(texture);
        }
      }
      MinecraftResourcePackWriter.minecraft().writeToDirectory(new File("test-resources"), pack);
    } catch (final IOException e) {
      throw new AssertionError(e);
    }
  }
}
