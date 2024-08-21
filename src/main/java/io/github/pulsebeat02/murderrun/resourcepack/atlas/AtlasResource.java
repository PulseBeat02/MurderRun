package io.github.pulsebeat02.murderrun.resourcepack.atlas;

import net.kyori.adventure.key.Key;
import team.unnamed.creative.atlas.Atlas;
import team.unnamed.creative.atlas.AtlasSource;

public final class AtlasResource {

  private final Key key;

  public AtlasResource() {
    this.key = Atlas.BLOCKS;
  }

  public Atlas stitchAtlas() {
    final AtlasSource source = this.getSource();
    return Atlas.atlas()
        .key(this.key)
        .addSource(source)
        .build();
  }

  private AtlasSource getSource() {
    return AtlasSource.directory("murderrun", "murderrun/");
  }

  public Key getKey() {
    return this.key;
  }
}
