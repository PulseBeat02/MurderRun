package io.github.pulsebeat02.murderrun.gson.adapters;

import com.google.gson.*;
import java.lang.reflect.Type;
import java.nio.file.Path;

public final class PathAdapter implements JsonDeserializer<Path>, JsonSerializer<Path> {

  @Override
  public Path deserialize(
    final JsonElement jsonElement,
    final Type type,
    final JsonDeserializationContext jsonDeserializationContext
  ) throws JsonParseException {
    final String path = jsonElement.getAsString();
    return Path.of(path);
  }

  @Override
  public JsonElement serialize(
    final Path path,
    final Type type,
    final JsonSerializationContext jsonSerializationContext
  ) {
    final String pathString = path.toString();
    return new JsonPrimitive(pathString);
  }
}
