package io.github.pulsebeat02.murderrun.json.adapters;

import com.google.gson.*;
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
    return BlockVector3.at(x.getAsInt(), y.getAsInt(), z.getAsInt());
  }

  @Override
  public JsonElement serialize(
      final BlockVector3 src, final Type typeOfSrc, final JsonSerializationContext context) {
    final JsonObject obj = new JsonObject();
    obj.addProperty("x", src.x());
    obj.addProperty("y", src.y());
    obj.addProperty("z", src.z());
    return obj;
  }
}
