package io.github.pulsebeat02.murderrun.resourcepack;

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
import team.unnamed.creative.ResourcePack;
import team.unnamed.creative.atlas.Atlas;
import team.unnamed.creative.base.Writable;
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
    this.pack.packMeta(34, "Murder Run Plugin Resources");
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
