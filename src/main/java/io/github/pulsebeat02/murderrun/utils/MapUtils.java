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

import static java.util.Objects.requireNonNull;

import java.nio.ByteBuffer;
import java.nio.charset.StandardCharsets;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.util.EulerAngle;

public final class MapUtils {

  private MapUtils() {
    throw new UnsupportedOperationException("Utility class cannot be instantiated");
  }

  public static Location getHighestSpawnLocation(final Location location) {
    final World world = requireNonNull(location.getWorld());
    final int x = location.getBlockX();
    final int z = location.getBlockZ();
    final int y = world.getHighestBlockYAt(x, z) + 1;
    return new Location(world, x, y, z);
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
    return new double[] { x, z };
  }

  private static double generateFriendlyDouble() {
    final double first = RandomUtils.generateDouble(0.5);
    final double second = RandomUtils.generateDouble(0.5);
    return first + second;
  }

  public static Location getAverageLocation(final Location first, final Location second) {
    final double x = (first.getX() + second.getX()) / 2;
    final double y = (first.getY() + second.getY()) / 2;
    final double z = (first.getZ() + second.getZ()) / 2;
    return new Location(first.getWorld(), x, y, z);
  }

  public static byte[] locationToByteArray(final Location location) {
    final World world = requireNonNull(location.getWorld());
    final String name = world.getName();
    final byte[] worldBytes = name.getBytes(StandardCharsets.UTF_8);
    final int length = Double.BYTES * 5 + Integer.BYTES + worldBytes.length;
    final ByteBuffer buffer = ByteBuffer.allocate(length);
    buffer.putInt(worldBytes.length);
    buffer.put(worldBytes);
    buffer.putDouble(location.getX());
    buffer.putDouble(location.getY());
    buffer.putDouble(location.getZ());
    buffer.putDouble(location.getPitch());
    buffer.putDouble(location.getYaw());

    return buffer.array();
  }

  public static Location byteArrayToLocation(final byte[] array) {
    final ByteBuffer buffer = ByteBuffer.wrap(array);
    final int worldNameLength = buffer.getInt();
    final byte[] worldBytes = new byte[worldNameLength];
    buffer.get(worldBytes);

    final String worldName = new String(worldBytes, StandardCharsets.UTF_8);
    final World world = Bukkit.getWorld(worldName);
    final double x = buffer.getDouble();
    final double y = buffer.getDouble();
    final double z = buffer.getDouble();
    final float pitch = (float) buffer.getDouble();
    final float yaw = (float) buffer.getDouble();

    return new Location(world, x, y, z, yaw, pitch);
  }
}
