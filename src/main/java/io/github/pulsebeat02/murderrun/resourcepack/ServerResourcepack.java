package io.github.pulsebeat02.murderrun.resourcepack;

import io.github.pulsebeat02.murderrun.resourcepack.model.ModelHandler;
import io.github.pulsebeat02.murderrun.resourcepack.sound.FXSound;
import io.github.pulsebeat02.murderrun.resourcepack.texture.ItemTexture;
import io.github.pulsebeat02.murderrun.utils.ResourceUtils;
import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import team.unnamed.creative.ResourcePack;
import team.unnamed.creative.base.Writable;
import team.unnamed.creative.serialize.minecraft.MinecraftResourcePackWriter;

public final class ServerResourcepack {

  private final Path path;

  private ResourcePack pack;

  public ServerResourcepack() {
    try {
      this.path = Files.createTempFile("murder-run-pack", ".zip");
    } catch (final IOException e) {
      throw new AssertionError(e);
    }
  }

  public void build() {
    this.pack = ResourcePack.resourcePack();
    this.customizeMetaData();
    this.addTextures();
    this.addModels();
    this.addSounds();
    this.zipPack();
  }

  private void customizeMetaData() {
    try {
      final InputStream stream = ResourceUtils.getResourceAsStream("assets/textures/pack.png");
      this.pack.packMeta(34, "Server resources for Murder Run Plugin");
      this.pack.icon(Writable.copyInputStream(stream));
    } catch (final IOException e) {
      throw new RuntimeException(e);
    }
  }

  private void addTextures() {
    final ItemTexture[] textures = ItemTexture.values();
    for (final ItemTexture texture : textures) {
      this.pack.texture(texture.getTexture());
    }
  }

  private void addModels() {
    final ModelHandler handler = new ModelHandler();
    this.pack.model(handler.customItemModelGenerator());
    this.pack.model(handler.customSwordGenerator());
    this.pack.model(handler.customJumpScareGenerator());
  }

  private void addSounds() {
    final FXSound[] sounds = FXSound.values();
    for (final FXSound sound : sounds) {
      this.pack.sound(sound.getSound());
    }
  }

  private void zipPack() {
    if (this.path == null) {
      throw new AssertionError("Failed to zip the server resource pack!");
    }
    MinecraftResourcePackWriter.minecraft().writeToZipFile(this.path, this.pack);
  }

  public ResourcePack getPack() {
    return this.pack;
  }

  public Path getPath() {
    return this.path;
  }
}
