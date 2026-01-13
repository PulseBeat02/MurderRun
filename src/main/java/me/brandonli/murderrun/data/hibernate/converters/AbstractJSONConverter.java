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
package me.brandonli.murderrun.data.hibernate.converters;

import com.google.common.reflect.TypeToken;
import com.google.gson.Gson;
import jakarta.persistence.AttributeConverter;
import jakarta.persistence.Converter;
import java.lang.reflect.Type;
import me.brandonli.murderrun.utils.gson.GsonProvider;
import org.checkerframework.checker.nullness.qual.Nullable;

@Converter(autoApply = true) // add annotation all subclasses
public abstract class AbstractJSONConverter<T> implements AttributeConverter<T, String> {

  private final TypeToken<T> token = new TypeToken<>(this.getClass()) {};

  @Override
  public @Nullable String convertToDatabaseColumn(final T data) {
    if (data == null) {
      return null;
    }

    // use json to serialize and deserialize data
    final Gson gson = GsonProvider.getGson();
    return gson.toJson(data);
  }

  @Override
  public @Nullable T convertToEntityAttribute(final String dbData) {
    if (dbData == null || dbData.isEmpty()) {
      return null;
    }

    final Gson gson = GsonProvider.getGson();
    final Type type = this.token.getType();
    return gson.fromJson(dbData, type);
  }
}
