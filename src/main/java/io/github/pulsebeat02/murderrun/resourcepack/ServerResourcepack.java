package io.github.pulsebeat02.murderrun.resourcepack;

import io.github.pulsebeat02.murderrun.resourcepack.model.ModelHandler;
import io.github.pulsebeat02.murderrun.resourcepack.sound.FXSound;
import io.github.pulsebeat02.murderrun.resourcepack.texture.ItemTexture;
import io.github.pulsebeat02.murderrun.utils.ResourceUtils;
import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Path;
import team.unnamed.creative.ResourcePack;
import team.unnamed.creative.base.Writable;
import team.unnamed.creative.serialize.minecraft.MinecraftResourcePackWriter;

public final class ServerResourcepack {

  private final ResourcePack pack;
  private final Path path;

  public ServerResourcepack() {
    this.pack = this.build();
    this.path = Path.of("murder-run-pack.zip");
  }

  private ResourcePack build() {
    final ResourcePack pack = ResourcePack.resourcePack();
    this.customizeMetaData(pack);
    this.addTextures(pack);
    this.addModels(pack);
    this.addSounds(pack);
    this.zipPack(pack);
    return pack;
  }

  private void customizeMetaData(final ResourcePack pack) {
    try {
      final InputStream stream = ResourceUtils.getResourceAsStream("assets/textures/pack.png");
      pack.packMeta(34, "Assets for Murder Run Plugin");
      pack.icon(Writable.copyInputStream(stream));
    } catch (final IOException e) {
      throw new RuntimeException(e);
    }
  }

  private void addTextures(final ResourcePack pack) {
    final ItemTexture[] textures = ItemTexture.values();
    for (final ItemTexture texture : textures) {
      pack.texture(texture.getTexture());
    }
  }

  private void addModels(final ResourcePack pack) {
    final ModelHandler handler = new ModelHandler();
    pack.model(handler.customItemModelGenerator());
    pack.model(handler.customItemModelGenerator());
  }

  private void addSounds(final ResourcePack pack) {
    final FXSound[] sounds = FXSound.values();
    for (final FXSound sound : sounds) {
      pack.sound(sound.getSound());
    }
  }

  private void zipPack(final ResourcePack pack) {
    MinecraftResourcePackWriter.minecraft().writeToZipFile(this.path, pack);
  }

  public ResourcePack getPack() {
    return this.pack;
  }

  public Path getPath() {
    return this.path;
  }
}
