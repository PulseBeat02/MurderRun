package io.github.pulsebeat02.murderrun.utils;

import java.util.SplittableRandom;

public final class RandomUtils {

  private static final SplittableRandom SPLITTABLE_RANDOM = new SplittableRandom();

  private RandomUtils() {
    throw new UnsupportedOperationException("Utility class cannot be instantiated");
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

  public static int generateInt(final int bound) {
    return SPLITTABLE_RANDOM.nextInt(bound);
  }

  public static int generateInt(final int min, final int max) {
    return SPLITTABLE_RANDOM.nextInt(min, max);
  }
}
