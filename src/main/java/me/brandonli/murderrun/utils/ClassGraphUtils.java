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

import io.github.classgraph.ClassGraph;
import io.github.classgraph.ClassInfo;
import io.github.classgraph.ClassInfoList;
import io.github.classgraph.ScanResult;
import java.util.List;

public final class ClassGraphUtils {

  private static final ScanResult SCAN_RESULT;

  static {
    SCAN_RESULT = new ClassGraph().enableClassInfo().scan(4);
  }

  public static void init() {
    // init
  }

  private ClassGraphUtils() {
    throw new UnsupportedOperationException("Utility class cannot be instantiated");
  }

  public static ScanResult getCachedScanResult() {
    return SCAN_RESULT;
  }

  public static Class<?> loadClass(final ClassInfo info) {
    try {
      return Class.forName(info.getName(), false, ClassGraphUtils.class.getClassLoader());
    } catch (final ClassNotFoundException e) {
      throw new IllegalArgumentException(e);
    }
  }

  public static <T> List<Class<? extends T>> loadClasses(
      final ClassInfoList infos, final Class<T> type) {
    return infos.stream()
        .<Class<? extends T>>map(info -> loadClass(info).asSubclass(type))
        .toList();
  }

  public static void close() {
    SCAN_RESULT.close();
  }
}
