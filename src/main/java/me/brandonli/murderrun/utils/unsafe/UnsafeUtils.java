/*

MIT License

Copyright (c) 2025 Brandon Li

Permission is hereby granted, free of charge, to any person obtaining a copy
of this software and associated documentation files (the "Software"), to deal
in the Software without restriction, including without limitation the rights
to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
copies of the Software, and to permit persons to whom the Software is
furnished to do so, subject to the following conditions:

The above copyright notice and this permission notice shall be included in all
copies or substantial portions of the Software.

THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE
SOFTWARE.

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
