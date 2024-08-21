package io.github.pulsebeat02.murderrun.resourcepack.model;

import static net.kyori.adventure.key.Key.key;

import io.github.pulsebeat02.murderrun.utils.IOUtils;
import net.kyori.adventure.key.Key;
import team.unnamed.creative.base.Writable;
import team.unnamed.creative.texture.Texture;

public final class JumpScareResource {

  private static final String PLUGIN_TEXTURE_RESOURCE = "textures/%s.png";

  private final Key key;
  private final String name;
  private final String path;

  public JumpScareResource() {
    this.name = "pumpkinblur";
    this.key = key("minecraft:misc/pumpkinblur.png");
    this.path = PLUGIN_TEXTURE_RESOURCE.formatted(this.name);
  }

  public Texture stitchTexture() {
    final Writable writable = IOUtils.getWritableStream(this.path);
    return Texture.texture().key(this.key).data(writable).build();
  }

  public String getName() {
    return this.name;
  }

  public String getPath() {
    return this.path;
  }
}
