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
package me.brandonli.murderrun.utils.gson.adapters;

import com.google.gson.JsonDeserializationContext;
import com.google.gson.JsonDeserializer;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParseException;
import com.google.gson.JsonSerializationContext;
import com.google.gson.JsonSerializer;
import com.sk89q.worldedit.math.BlockVector3;
import java.lang.reflect.Type;

public final class BlockVectorAdapter implements JsonSerializer<BlockVector3>, JsonDeserializer<BlockVector3> {

  @Override
  public BlockVector3 deserialize(final JsonElement jsonString, final Type typeOfT, final JsonDeserializationContext context)
    throws JsonParseException {
    final JsonObject obj = (JsonObject) jsonString;
    final JsonElement x = obj.get("x");
    final JsonElement y = obj.get("y");
    final JsonElement z = obj.get("z");
    final int xValue = x.getAsInt();
    final int yValue = y.getAsInt();
    final int zValue = z.getAsInt();
    return BlockVector3.at(xValue, yValue, zValue);
  }

  @Override
  public JsonElement serialize(final BlockVector3 src, final Type typeOfSrc, final JsonSerializationContext context) {
    final JsonObject obj = new JsonObject();
    final int x = src.x();
    final int y = src.y();
    final int z = src.z();
    obj.addProperty("x", x);
    obj.addProperty("y", y);
    obj.addProperty("z", z);
    return obj;
  }
}
