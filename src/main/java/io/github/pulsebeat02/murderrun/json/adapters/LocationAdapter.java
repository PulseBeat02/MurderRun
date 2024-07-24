package io.github.pulsebeat02.murderrun.json.adapters;

import com.google.gson.*;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.World;

import java.lang.reflect.Type;

public final class LocationAdapter implements JsonDeserializer<Location>, JsonSerializer<Location> {

  @Override
  public Location deserialize(
      final JsonElement jsonString,
      final Type type,
      final JsonDeserializationContext jsonDeserializationContext)
      throws JsonParseException {
    final JsonObject obj = (JsonObject) jsonString;
    final JsonElement world = obj.get("world");
    final JsonElement x = obj.get("x");
    final JsonElement y = obj.get("y");
    final JsonElement z = obj.get("z");
    final JsonElement yaw = obj.get("yaw");
    final JsonElement pitch = obj.get("pitch");
    final World instance = Bukkit.getWorld(world.getAsString());
    return new Location(
        instance,
        x.getAsDouble(),
        y.getAsDouble(),
        z.getAsDouble(),
        yaw != null ? yaw.getAsFloat() : 0.0F,
        pitch != null ? pitch.getAsFloat() : 0.0F);
  }

  @Override
  public JsonElement serialize(
      final Location location,
      final Type type,
      final JsonSerializationContext jsonSerializationContext) {
    final JsonObject obj = new JsonObject();
    obj.addProperty("world", location.getWorld().getName());
    obj.addProperty("x", location.getX());
    obj.addProperty("y", location.getY());
    obj.addProperty("z", location.getZ());
    obj.addProperty("yaw", location.getYaw());
    obj.addProperty("pitch", location.getPitch());
    return obj;
  }
}
