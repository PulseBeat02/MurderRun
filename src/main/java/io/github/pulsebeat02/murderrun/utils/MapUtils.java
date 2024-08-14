package io.github.pulsebeat02.murderrun.utils;

import com.sk89q.worldedit.math.BlockVector3;
import org.bukkit.Location;
import org.bukkit.util.EulerAngle;

public final class MapUtils {

  private MapUtils() {
    throw new UnsupportedOperationException("Utility class cannot be instantiated");
  }

  public static EulerAngle toEulerAngle(final int x, final int y, final int z) {
    final double radianX = Math.toRadians(x);
    final double radianY = Math.toRadians(y);
    final double radianZ = Math.toRadians(z);
    return new EulerAngle(radianX, radianY, radianZ);
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

  public static BlockVector3 toBlockVector3(final Location location) {
    final double x = location.getX();
    final double y = location.getY();
    final double z = location.getZ();
    return BlockVector3.at(x, y, z);
  }

  public static Location getAverageLocation(final Location first, final Location second) {
    final double x = (first.getX() + second.getX()) / 2;
    final double y = (first.getY() + second.getY()) / 2;
    final double z = (first.getZ() + second.getZ()) / 2;
    return new Location(first.getWorld(), x, y, z);
  }
}
