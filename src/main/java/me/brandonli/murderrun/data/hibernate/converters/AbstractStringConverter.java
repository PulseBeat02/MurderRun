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

import jakarta.persistence.AttributeConverter;
import jakarta.persistence.Converter;
import org.checkerframework.checker.nullness.qual.Nullable;

@Converter(autoApply = true) // add annotation all subclasses
public abstract class AbstractStringConverter<T> implements AttributeConverter<T, String> {

  @Override
  public @Nullable String convertToDatabaseColumn(final T data) {
    if (data == null) {
      return null;
    }
    return data.toString();
  }

  @Override
  public @Nullable T convertToEntityAttribute(final String dbData) {
    if (dbData == null || dbData.isEmpty()) {
      return null;
    }
    return this.fromString(dbData);
  }

  public abstract T fromString(final String dbData);
}
