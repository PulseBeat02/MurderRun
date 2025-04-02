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
package me.brandonli.murderrun.utils.item;

import org.bukkit.NamespacedKey;
import org.bukkit.persistence.PersistentDataContainer;
import org.bukkit.persistence.PersistentDataType;

public final class MetadataHelper {

  private final PersistentDataContainer container;

  public MetadataHelper(final PersistentDataContainer container) {
    this.container = container;
  }

  public void set(final NamespacedKey key, final Object value) {
    switch (value) {
      case final Byte b -> this.container.set(key, PersistentDataType.BYTE, b);
      case final Short i -> this.container.set(key, PersistentDataType.SHORT, i);
      case final Integer i -> this.container.set(key, PersistentDataType.INTEGER, i);
      case final Long l -> this.container.set(key, PersistentDataType.LONG, l);
      case final Float v -> this.container.set(key, PersistentDataType.FLOAT, v);
      case final Double v -> this.container.set(key, PersistentDataType.DOUBLE, v);
      case final Boolean b -> this.container.set(key, PersistentDataType.BOOLEAN, b);
      case final String s -> this.container.set(key, PersistentDataType.STRING, s);
      case final byte[] bytes -> this.container.set(key, PersistentDataType.BYTE_ARRAY, bytes);
      case final int[] ints -> this.container.set(key, PersistentDataType.INTEGER_ARRAY, ints);
      case final long[] longs -> this.container.set(key, PersistentDataType.LONG_ARRAY, longs);
      case null, default -> throw new IllegalArgumentException("Unsupported data type");
    }
  }
}
