package io.github.pulsebeat02.murderrun.utils;

import org.bukkit.Location;

public final class MapUtils {

  private MapUtils() {
    throw new UnsupportedOperationException("Utility class cannot be instantiated");
  }

  public static double[] generateFriendlyRandomXZ(final Location first, final Location second) {
    final double x = first.getX() + (second.getX() - first.getX()) * generateFriendlyDouble();
    final double z = first.getZ() + (second.getZ() - first.getZ()) * generateFriendlyDouble();
    return new double[] {x, z};
  }

  private static double generateFriendlyDouble() {
    final double first = RandomUtils.generateDouble(0.5);
    final double second = RandomUtils.generateDouble(0.5);
    return first + second;
  }
}
