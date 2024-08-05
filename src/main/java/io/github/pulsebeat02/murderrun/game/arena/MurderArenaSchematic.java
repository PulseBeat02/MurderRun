package io.github.pulsebeat02.murderrun.game.arena;

import com.sk89q.worldedit.math.BlockVector3;
import java.nio.file.Path;

public final class MurderArenaSchematic {

  private final Path schematicPath;
  private final BlockVector3 origin;

  public MurderArenaSchematic(final Path schematicPath, final BlockVector3 origin) {
    this.schematicPath = schematicPath;
    this.origin = origin;
  }

  public Path getSchematicPath() {
    return this.schematicPath;
  }

  public BlockVector3 getOrigin() {
    return this.origin;
  }
}
