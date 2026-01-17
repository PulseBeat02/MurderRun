/*
 * This file is part of Murder Run, a spin-off game-mode of Dead by Daylight
 * Copyright (C) Brandon Li <https://brandonli.me/>
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program.  If not, see <https://www.gnu.org/licenses/>.
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
        if (extension.equals("png")
            && !name.startsWith("diamond_layer")
            && !name.startsWith("bow_pulling")) {
          final Texture texture = Texture.texture()
              .key(Key.key("murderrun", "item/" + name))
              .data(Writable.path(file))
              .build();
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
              Writable.stringUtf8("""
              {
                "model": {
                  "type": "minecraft:model",
                  "model": "murderrun:item/%s"
                }
              }""".formatted(nameWithoutExtension)));
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
