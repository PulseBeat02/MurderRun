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
package me.brandonli.murderrun.utils.immutable;

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

  public SerializableVector(final SerializableVector other) {
    this.x = other.x;
    this.y = other.y;
    this.z = other.z;
  }

  public BlockVector3 getVector3() {
    return BlockVector3.at(this.x, this.y, this.z);
  }
}
