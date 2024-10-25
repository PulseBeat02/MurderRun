package io.github.pulsebeat02.murderrun.gson.adapters;

import static java.util.Objects.requireNonNull;

import com.google.gson.JsonDeserializationContext;
import com.google.gson.JsonDeserializer;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParseException;
import com.google.gson.JsonSerializationContext;
import com.google.gson.JsonSerializer;
import java.lang.reflect.Type;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.WorldCreator;

public final class LocationAdapter implements JsonDeserializer<Location>, JsonSerializer<Location> {

  @Override
  public Location deserialize(final JsonElement jsonString, final Type type, final JsonDeserializationContext jsonDeserializationContext)
    throws JsonParseException {
    final JsonObject obj = (JsonObject) jsonString;
    final JsonElement world = obj.get("world");
    final JsonElement x = obj.get("x");
    final JsonElement y = obj.get("y");
    final JsonElement z = obj.get("z");
    final JsonElement yaw = obj.get("yaw");
    final JsonElement pitch = obj.get("pitch");
    final String worldName = world.getAsString();
    final World instance = this.loadWorld(worldName);
    final double xValue = x.getAsDouble();
    final double yValue = y.getAsDouble();
    final double zValue = z.getAsDouble();
    final float yawValue = yaw != null ? yaw.getAsFloat() : 0.0f;
    final float pitchValue = pitch != null ? pitch.getAsFloat() : 0.0f;
    return new Location(instance, xValue, yValue, zValue, yawValue, pitchValue);
  }

  private World loadWorld(final String name) {
    final World temporary = Bukkit.getWorld(name);
    if (temporary == null) {
      final WorldCreator creator = new WorldCreator(name);
      return requireNonNull(Bukkit.createWorld(creator), "World could not be created");
    }
    return temporary;
  }

  @Override
  public JsonElement serialize(final Location location, final Type type, final JsonSerializationContext jsonSerializationContext) {
    final JsonObject obj = new JsonObject();
    final World world = requireNonNull(location.getWorld());
    final String name = world.getName();
    final double x = location.getX();
    final double y = location.getY();
    final double z = location.getZ();
    final float yaw = location.getYaw();
    final float pitch = location.getPitch();
    obj.addProperty("world", name);
    obj.addProperty("x", x);
    obj.addProperty("y", y);
    obj.addProperty("z", z);
    obj.addProperty("yaw", yaw);
    obj.addProperty("pitch", pitch);
    return obj;
  }
}
