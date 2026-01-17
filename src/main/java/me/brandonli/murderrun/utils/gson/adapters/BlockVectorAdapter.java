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

public final class BlockVectorAdapter
    implements JsonSerializer<BlockVector3>, JsonDeserializer<BlockVector3> {

  @Override
  public BlockVector3 deserialize(
      final JsonElement jsonString, final Type typeOfT, final JsonDeserializationContext context)
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
  public JsonElement serialize(
      final BlockVector3 src, final Type typeOfSrc, final JsonSerializationContext context) {
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
