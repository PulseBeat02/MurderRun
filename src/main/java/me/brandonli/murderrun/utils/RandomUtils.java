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

import com.google.common.collect.Iterables;
import java.util.Collection;
import java.util.SplittableRandom;
import org.intellij.lang.annotations.Subst;

public final class RandomUtils {

  private static final SplittableRandom SPLITTABLE_RANDOM = new SplittableRandom();

  private RandomUtils() {
    throw new UnsupportedOperationException("Utility class cannot be instantiated");
  }

  public static <T> T getRandomElement(final Collection<T> from) {
    final int size = from.size();
    if (size == 0) {
      throw new IllegalArgumentException("Cannot get random element from empty collection");
    }
    final int i = generateInt(size);
    return Iterables.get(from, i);
  }

  public static double generateDouble(final double bound) {
    return SPLITTABLE_RANDOM.nextDouble(bound);
  }

  public static float generateFloat() {
    return SPLITTABLE_RANDOM.nextFloat();
  }

  public static double generateDouble() {
    return SPLITTABLE_RANDOM.nextDouble();
  }

  @Subst("")
  public static int generateInt(final int bound) {
    return SPLITTABLE_RANDOM.nextInt(bound);
  }

  public static int generateInt(final int min, final int max) {
    return SPLITTABLE_RANDOM.nextInt(min, max);
  }
}
