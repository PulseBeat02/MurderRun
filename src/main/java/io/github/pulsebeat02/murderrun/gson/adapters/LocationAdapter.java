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
      final World world = Bukkit.createWorld(creator);
      return requireNonNull(world, "World could not be created");
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
