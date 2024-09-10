package io.github.pulsebeat02.murderrun.gson.adapters;

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
