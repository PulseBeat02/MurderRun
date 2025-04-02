/*

MIT License

Copyright (c) 2024 Brandon Li

Permission is hereby granted, free of charge, to any person obtaining a copy
of this software and associated documentation files (the "Software"), to deal
in the Software without restriction, including without limitation the rights
to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
copies of the Software, and to permit persons to whom the Software is
furnished to do so, subject to the following conditions:

The above copyright notice and this permission notice shall be included in all
copies or substantial portions of the Software.

THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE
SOFTWARE.

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
