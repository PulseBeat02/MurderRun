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

import com.google.gson.*;
import java.lang.reflect.Type;
import java.nio.file.Path;

public final class PathAdapter implements JsonDeserializer<Path>, JsonSerializer<Path> {

  @Override
  public Path deserialize(
      final JsonElement jsonElement,
      final Type type,
      final JsonDeserializationContext jsonDeserializationContext)
      throws JsonParseException {
    final String path = jsonElement.getAsString();
    return Path.of(path);
  }

  @Override
  public JsonElement serialize(
      final Path path, final Type type, final JsonSerializationContext jsonSerializationContext) {
    final String pathString = path.toString();
    return new JsonPrimitive(pathString);
  }
}
