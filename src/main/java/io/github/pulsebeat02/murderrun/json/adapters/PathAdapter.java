package io.github.pulsebeat02.murderrun.json.adapters;

import com.google.gson.*;
import java.lang.reflect.Type;
import java.nio.file.Path;
import java.nio.file.Paths;

public final class PathAdapter implements JsonDeserializer<Path>, JsonSerializer<Path> {

  @Override
  public Path deserialize(
      final JsonElement jsonElement,
      final Type type,
      final JsonDeserializationContext jsonDeserializationContext)
      throws JsonParseException {
    return Paths.get(jsonElement.getAsString());
  }

  @Override
  public JsonElement serialize(
      final Path path, final Type type, final JsonSerializationContext jsonSerializationContext) {
    return new JsonPrimitive(path.toString());
  }
}
