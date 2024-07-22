package io.github.pulsebeat02.murderrun.resourcepack;

import io.github.pulsebeat02.murderrun.resourcepack.model.ModelHandler;
import io.github.pulsebeat02.murderrun.resourcepack.texture.ItemTexture;
import io.github.pulsebeat02.murderrun.resourcepack.sound.FXSound;
import team.unnamed.creative.ResourcePack;
import team.unnamed.creative.serialize.minecraft.MinecraftResourcePackWriter;

import java.nio.file.Path;

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
    pack.packMeta(34, "Assets for Murder Run Plugin");
  }

  private void addTextures(final ResourcePack pack) {
    final ItemTexture[] textures = ItemTexture.values();
    for (final ItemTexture texture : textures) {
      pack.texture(texture.getTexture());
    }
  }

  private void addSounds(final ResourcePack pack) {
    final FXSound[] sounds = FXSound.values();
    for (final FXSound sound : sounds) {
      pack.sound(sound.getSound());
    }
  }

  private void addModels(final ResourcePack pack) {
    final ModelHandler handler = new ModelHandler();
    pack.model(handler.diamondModel());
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
