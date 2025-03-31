/*

MIT License

Copyright (c) 2024 Brandon Li

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
package io.github.pulsebeat02.murderrun.utils;

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
