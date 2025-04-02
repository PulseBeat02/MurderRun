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
package me.brandonli.murderrun.utils.unsafe;

import java.lang.reflect.Field;
import java.util.Map;
import org.checkerframework.checker.nullness.qual.Nullable;
import sun.misc.Unsafe;

@SuppressWarnings({ "deprecation", "unchecked" })
public final class UnsafeUtils {

  private static final Unsafe UNSAFE = UnsafeProvider.getUnsafe();

  private UnsafeUtils() {
    throw new UnsupportedOperationException("Utility class cannot be instantiated");
  }

  @SuppressWarnings("all") // checker
  public static void setFinalField(final Field field, final Object obj, final @Nullable Object value) {
    UNSAFE.putObject(obj, UNSAFE.objectFieldOffset(field), value);
  }

  @SuppressWarnings("all") // checker
  public static void setStaticFinalField(final Field field, final @Nullable Object value) {
    UNSAFE.putObject(UNSAFE.staticFieldBase(field), UNSAFE.staticFieldOffset(field), value);
  }

  public static Object getFieldExceptionally(final Object object, final String name) {
    try {
      return getField(object, name);
    } catch (final NoSuchFieldException e) {
      throw new AssertionError(e);
    }
  }

  public static Object getFieldExceptionally(final Class<?> clazz, final Object object, final String name) {
    try {
      return getField(clazz, object, name);
    } catch (final NoSuchFieldException e) {
      throw new AssertionError(e);
    }
  }

  public static Object getField(final Object object, final String name) throws NoSuchFieldException {
    return getField(object.getClass(), object, name);
  }

  public static Object getField(final Class<?> clazz, final Object object, final String name) throws NoSuchFieldException {
    return UNSAFE.getObject(object, UNSAFE.objectFieldOffset(clazz.getDeclaredField(name)));
  }

  public static void setEnvironmentalVariable(final String key, final String value) {
    try {
      final Map<String, String> unwritable = System.getenv();
      final Map<String, String> writable = (Map<String, String>) UnsafeUtils.getField(unwritable, "m");
      writable.put(key, value);
    } catch (final NoSuchFieldException e) {
      throw new AssertionError(e);
    }
  }
}
