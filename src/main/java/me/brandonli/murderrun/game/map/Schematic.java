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
package me.brandonli.murderrun.game.map;

import com.sk89q.worldedit.WorldEditException;
import com.sk89q.worldedit.extent.clipboard.Clipboard;
import com.sk89q.worldedit.math.BlockVector3;
import jakarta.persistence.Column;
import jakarta.persistence.Convert;
import java.io.IOException;
import java.io.Serial;
import java.io.Serializable;
import me.brandonli.murderrun.data.hibernate.converters.SerializableVectorConverter;
import me.brandonli.murderrun.utils.immutable.SerializableVector;
import me.brandonli.murderrun.utils.map.MapUtils;
import org.bukkit.Location;

public final class Schematic implements Serializable {

  @Serial
  private static final long serialVersionUID = 4953428050756665476L;

  @Column(name = "schematic_path")
  private final String schematicPath;

  @Convert(converter = SerializableVectorConverter.class)
  @Column(name = "origin")
  private final SerializableVector origin;

  private transient Clipboard clipboard;

  public Schematic(final String schematicPath, final SerializableVector origin) {
    this.schematicPath = schematicPath;
    this.origin = origin;
  }

  public Schematic(final Schematic schematic) {
    this.schematicPath = schematic.schematicPath;
    this.origin = new SerializableVector(schematic.origin);
    this.clipboard = schematic.clipboard;
  }

  public static Schematic copyAndCreateSchematic(final String name, final Location[] corners, final boolean arena) {
    try {
      final Clipboard clipboard = MapUtils.performForwardExtentCopy(corners);
      final String path = MapUtils.performSchematicWrite(clipboard, name, arena);
      final BlockVector3 origin = clipboard.getOrigin();
      final SerializableVector serializable = new SerializableVector(origin);
      return new Schematic(path, serializable);
    } catch (final WorldEditException | IOException e) {
      throw new AssertionError(e);
    }
  }

  public String getSchematicPath() {
    return this.schematicPath;
  }

  public SerializableVector getOrigin() {
    return this.origin;
  }

  public void loadSchematicIntoMemory() {
    if (this.clipboard == null) {
      try {
        this.clipboard = MapUtils.loadSchematic(this);
      } catch (final IOException e) {
        throw new AssertionError(e);
      }
    }
  }

  public Clipboard getClipboard() {
    return this.clipboard;
  }
}
