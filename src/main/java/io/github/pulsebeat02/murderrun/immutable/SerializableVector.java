package io.github.pulsebeat02.murderrun.immutable;

import com.sk89q.worldedit.math.BlockVector3;
import java.io.Serial;
import java.io.Serializable;

public final class SerializableVector implements Serializable {

  @Serial
  private static final long serialVersionUID = 1085356780665532881L;

  private final int x;
  private final int y;
  private final int z;

  public SerializableVector(final BlockVector3 vector3) {
    this.x = vector3.x();
    this.y = vector3.y();
    this.z = vector3.z();
  }

  public BlockVector3 getVector3() {
    return BlockVector3.at(this.x, this.y, this.z);
  }
}
