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
package io.github.pulsebeat02.murderrun.resourcepack;

import io.github.pulsebeat02.murderrun.locale.Message;
import io.github.pulsebeat02.murderrun.resourcepack.atlas.AtlasResource;
import io.github.pulsebeat02.murderrun.resourcepack.model.ItemModel;
import io.github.pulsebeat02.murderrun.resourcepack.model.ItemResource;
import io.github.pulsebeat02.murderrun.resourcepack.model.ItemTexture;
import io.github.pulsebeat02.murderrun.resourcepack.model.Items;
import io.github.pulsebeat02.murderrun.resourcepack.model.JumpScareResource;
import io.github.pulsebeat02.murderrun.resourcepack.model.Models;
import io.github.pulsebeat02.murderrun.resourcepack.sound.SoundFile;
import io.github.pulsebeat02.murderrun.resourcepack.sound.SoundResource;
import io.github.pulsebeat02.murderrun.resourcepack.sound.Sounds;
import io.github.pulsebeat02.murderrun.utils.IOUtils;
import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Path;
import java.util.Set;
import net.kyori.adventure.text.Component;
import team.unnamed.creative.ResourcePack;
import team.unnamed.creative.atlas.Atlas;
import team.unnamed.creative.base.Writable;
import team.unnamed.creative.metadata.pack.PackFormat;
import team.unnamed.creative.model.Model;
import team.unnamed.creative.serialize.minecraft.MinecraftResourcePackWriter;
import team.unnamed.creative.sound.Sound;
import team.unnamed.creative.sound.SoundEvent;
import team.unnamed.creative.texture.Texture;

public final class PackWrapper {

  private static final String PACK_PNG_PATH = "textures/pack.png";

  private final ResourcePack pack;
  private final Path path;

  public PackWrapper(final Path path) {
    this.pack = ResourcePack.resourcePack();
    this.path = path;
  }

  public void wrapPack() throws IOException {
    this.addMetadata();
    this.addSounds();
    this.addItems();
    this.addModels();
    this.addAtlases();
    this.addSpecialResources();
    this.zipPack();
  }

  private void addSpecialResources() {
    final JumpScareResource resource = new JumpScareResource();
    final Texture texture = resource.stitchTexture();
    this.pack.texture(texture);
  }

  private void addAtlases() {
    final AtlasResource resource = new AtlasResource();
    final Atlas atlas = resource.stitchAtlas();
    this.pack.atlas(atlas);
  }

  private void zipPack() {
    final MinecraftResourcePackWriter writer = MinecraftResourcePackWriter.minecraft();
    writer.writeToZipFile(this.path, this.pack);
  }

  private void addMetadata() throws IOException {
    final InputStream stream = IOUtils.getResourceAsStream(PACK_PNG_PATH);
    final PackFormat format = PackFormat.format(42, 34, 42);
    final Component component = Message.RESOURCE_PACK_META.build();
    this.pack.packMeta(format, component);
    this.pack.icon(Writable.copyInputStream(stream));
  }

  private void addSounds() {
    final Set<SoundResource> sounds = Sounds.getAllSounds();
    for (final SoundResource sound : sounds) {
      final SoundFile file = sound.getSound();
      final Sound stitched = file.stitchSound();
      final SoundEvent event = sound.stitchSound();
      this.pack.sound(stitched);
      this.pack.soundEvent(event);
    }
  }

  private void addItems() {
    final Set<ItemResource> items = Items.getAllVanillaItemModels();
    for (final ItemResource item : items) {
      final Model vanilla = item.createVanillaModel();
      this.pack.model(vanilla);
    }
  }

  private void addModels() {
    final Set<ItemModel> models = Models.getAllModels();
    for (final ItemModel model : models) {
      final ItemTexture item = model.getTexture();
      final Model stitched = model.stitchModel();
      final Texture texture = item.stitchTexture();
      this.pack.model(stitched);
      this.pack.texture(texture);
    }
  }
}
