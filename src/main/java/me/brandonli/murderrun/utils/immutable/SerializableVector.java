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
