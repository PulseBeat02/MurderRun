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
package me.brandonli.murderrun.utils;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.function.*;
import java.util.stream.Collector;
import java.util.stream.Collectors;

public final class StreamUtils {

  private StreamUtils() {
    throw new UnsupportedOperationException("Utility class cannot be instantiated");
  }

  public static <T> Predicate<T> notEquals(final T compare) {
    return first -> first != null && !first.equals(compare);
  }

  public static <T> Predicate<T> inverse(final Predicate<T> predicate) {
    return predicate.negate();
  }

  public static <T, U> Predicate<T> isInstanceOf(final Class<U> clazz) {
    return clazz::isInstance;
  }

  @SuppressWarnings("unchecked")
  public static <T> Collector<T, ?, List<T>> toShuffledList() {
    return Collectors.collectingAndThen(Collectors.toCollection(ArrayList::new), list -> {
      Collections.shuffle(list);
      return list;
    });
  }

  public static <T> Collector<T, ?, Set<T>> toSynchronizedSet() {
    return Collectors.toCollection(() -> Collections.synchronizedSet(new HashSet<>()));
  }
}
