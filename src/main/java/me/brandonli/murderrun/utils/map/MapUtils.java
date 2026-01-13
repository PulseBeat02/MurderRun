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
package me.brandonli.murderrun.utils.map;

import static java.util.Objects.requireNonNull;

import com.sk89q.worldedit.EditSession;
import com.sk89q.worldedit.WorldEdit;
import com.sk89q.worldedit.WorldEditException;
import com.sk89q.worldedit.bukkit.BukkitAdapter;
import com.sk89q.worldedit.extent.clipboard.BlockArrayClipboard;
import com.sk89q.worldedit.extent.clipboard.Clipboard;
import com.sk89q.worldedit.extent.clipboard.io.*;
import com.sk89q.worldedit.function.operation.ForwardExtentCopy;
import com.sk89q.worldedit.function.operation.Operation;
import com.sk89q.worldedit.function.operation.Operations;
import com.sk89q.worldedit.math.BlockVector3;
import com.sk89q.worldedit.regions.CuboidRegion;
import com.sk89q.worldedit.regions.Region;
import com.sk89q.worldedit.util.SideEffect;
import it.unimi.dsi.fastutil.io.FastBufferedInputStream;
import it.unimi.dsi.fastutil.io.FastBufferedOutputStream;
import java.io.*;
import java.nio.ByteBuffer;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.*;
import java.util.concurrent.CompletableFuture;
import me.brandonli.murderrun.MurderRun;
import me.brandonli.murderrun.game.GameProperties;
import me.brandonli.murderrun.game.capability.Capabilities;
import me.brandonli.murderrun.game.extension.worldedit.WESpreader;
import me.brandonli.murderrun.game.map.Schematic;
import me.brandonli.murderrun.utils.IOUtils;
import me.brandonli.murderrun.utils.RandomUtils;
import me.brandonli.murderrun.utils.immutable.SerializableVector;
import org.bukkit.*;
import org.bukkit.block.Block;
import org.bukkit.block.BlockFace;
import org.bukkit.generator.ChunkGenerator;
import org.bukkit.plugin.java.JavaPlugin;
import org.bukkit.util.EulerAngle;
import org.checkerframework.checker.nullness.qual.NonNull;
import org.incendo.cloud.type.tuple.Triplet;

public final class MapUtils {

  private static final Set<SideEffect> DISABLED_SIDE_EFFECTS = Set.of(SideEffect.UPDATE, SideEffect.NEIGHBORS);
  private static final String WE_SPREADER = "worldedit.spreader.enabled";
  private static final Properties SERVER_PROPERTIES = new Properties();

  static {
    try {
      SERVER_PROPERTIES.load(new FileInputStream("server.properties"));
    } catch (final IOException e) {
      throw new AssertionError(e);
    }
  }

  private MapUtils() {
    throw new UnsupportedOperationException("Utility class cannot be instantiated");
  }

  public static Location getSafeSpawn(final Location location) {
    final Block block = location.getBlock();
    final Material material = block.getType();
    if (material.isAir()) {
      return location;
    }
    return location.add(0, 1, 0);
  }

  public static Triplet<Integer, Integer, Integer> toPosTriplet(final BlockFace face) {
    final int x = face.getModX();
    final int y = face.getModY();
    final int z = face.getModZ();
    return Triplet.of(x, y, z);
  }

  public static World getMainWorld() {
    final String name = SERVER_PROPERTIES.getProperty("level-name", "world");
    return requireNonNull(Bukkit.getWorld(name));
  }

  public static Location[] copyLocationArray(final Location... locations) {
    final int length = locations.length;
    final Location[] copy = new Location[length];
    for (int i = 0; i < copy.length; i++) {
      final Location location = locations[i];
      final Location clone = location.clone();
      copy[i] = clone;
    }
    return copy;
  }

  public static World createVoidWorld(final String name, final World copy) {
    final ChunkGenerator generator = new FastChunkGenerator();
    final World.Environment environment = copy.getEnvironment();
    final World world = new WorldCreator(name).environment(environment).generator(generator).createWorld();
    requireNonNull(world);

    final Registry<@NonNull GameRule<?>> gameRuleRegistry = Registry.GAME_RULE;
    for (final GameRule<?> gameRule : gameRuleRegistry) {
      copyRule(copy, world, gameRule);
    }

    Difficulty oldDifficulty = copy.getDifficulty();
    if (oldDifficulty == Difficulty.PEACEFUL) {
      oldDifficulty = Difficulty.NORMAL;
    }
    world.setDifficulty(oldDifficulty);

    final long time = copy.getTime();
    world.setTime(time);

    return requireNonNull(world);
  }

  private static <T> void copyRule(final World old, final World after, final GameRule<T> rule) {
    final NamespacedKey key = rule.getKey();
    final String name = key.getKey();
    if (!old.isGameRule(name)) {
      return;
    }
    final T value = old.getGameRuleValue(rule);
    if (value == null) {
      return;
    }
    after.setGameRule(rule, value);
  }

  public static boolean enableExtent() {
    final String property = System.getProperty(WE_SPREADER);
    final boolean enabled = Boolean.parseBoolean(property);
    if (enabled) { // disabled always
      System.setProperty(WE_SPREADER, "true");
      final MurderRun plugin = (MurderRun) JavaPlugin.getProvidingPlugin(MurderRun.class); // special case
      final WESpreader spreader = new WESpreader(plugin);
      spreader.load();
      return true;
    }
    return false;
  }

  public static CompletableFuture<Void> performPaste(
    final GameProperties properties,
    final com.sk89q.worldedit.world.World world,
    final Clipboard clipboard,
    final SerializableVector vector3
  ) {
    final MurderRun plugin = (MurderRun) JavaPlugin.getProvidingPlugin(MurderRun.class);
    if (Capabilities.FASTASYNCWORLDEDIT.isEnabled()) { // use normal paste
      final Region region = clipboard.getRegion();
      final BlockVector3 vec = vector3.getVector3();
      final ForwardExtentCopy op = new ForwardExtentCopy(clipboard, region, world, vec);
      op.setCopyingEntities(true);
      try {
        Operations.complete(op);
        return CompletableFuture.completedFuture(null);
      } catch (final WorldEditException e) {
        final CompletableFuture<Void> failed = new CompletableFuture<>();
        failed.completeExceptionally(e);
        return failed;
      }
    }
    final Collection<Operation> operations = splitClipboardOperation(world, clipboard, vector3);
    final Iterator<Operation> iterator = operations.iterator();
    final CompletableFuture<Void> future = new CompletableFuture<>();
    final OperationRunnable runnable = new OperationRunnable(properties, iterator, future);
    runnable.runTaskTimer(plugin, 1L, 1L);
    return future;
  }

  private static Collection<Operation> splitClipboardOperation(
    final com.sk89q.worldedit.world.World world,
    final Clipboard clipboard,
    final SerializableVector vector3
  ) {
    final BlockVector3 dimensions = clipboard.getDimensions();
    final int width = dimensions.x();
    final int height = dimensions.y();
    final int length = dimensions.z();
    final int chunkSize = 16;
    final int xChunks = (int) Math.ceil((double) width / chunkSize);
    final int yChunks = (int) Math.ceil((double) height / chunkSize);
    final int zChunks = (int) Math.ceil((double) length / chunkSize);
    final Collection<Operation> operations = new ArrayList<>();
    final BlockVector3 origin = clipboard.getOrigin();
    final BlockVector3 destPos = vector3.getVector3();
    for (int x = 0; x < xChunks; x++) {
      for (int y = 0; y < yChunks; y++) {
        for (int z = 0; z < zChunks; z++) {
          final int startX = x * chunkSize;
          final int startY = y * chunkSize;
          final int startZ = z * chunkSize;
          final int endX = Math.min(startX + chunkSize, width);
          final int endY = Math.min(startY + chunkSize, height);
          final int endZ = Math.min(startZ + chunkSize, length);
          final BlockVector3 min = BlockVector3.at(startX, startY, startZ);
          final BlockVector3 max = BlockVector3.at(endX - 1, endY - 1, endZ - 1);
          final BlockVector3 first = origin.add(min);
          final BlockVector3 second = origin.add(max);
          final CuboidRegion region = new CuboidRegion(first, second);
          final BlockVector3 offset = destPos.add(min);
          final ForwardExtentCopy op = new ForwardExtentCopy(clipboard, region, world, offset);
          op.setCopyingEntities(true);
          operations.add(op);
        }
      }
    }
    return operations;
  }

  public static Clipboard loadSchematic(final Schematic schematic) throws IOException {
    final String path = schematic.getSchematicPath();
    final File legacyPath = new File(path);
    final ClipboardFormat format = requireNonNull(ClipboardFormats.findByFile(legacyPath));
    try (
      final InputStream stream = new FileInputStream(legacyPath);
      final FastBufferedInputStream fast = new FastBufferedInputStream(stream);
      final ClipboardReader reader = format.getReader(fast)
    ) {
      return reader.read();
    }
  }

  public static String performSchematicWrite(final Clipboard clipboard, final String name, final boolean arena) throws IOException {
    final String folder = arena ? "arenas" : "lobbies";
    final Path data = IOUtils.getPluginDataFolderPath();
    final Path parent = data.resolve("schematics");
    final Path folderPath = parent.resolve(folder);
    IOUtils.createFolder(folderPath);

    final Path file = folderPath.resolve(name);
    final BuiltInClipboardFormat format = BuiltInClipboardFormat.SPONGE_V3_SCHEMATIC;
    try (
      final OutputStream stream = Files.newOutputStream(file);
      final OutputStream fast = new FastBufferedOutputStream(stream);
      final ClipboardWriter writer = format.getWriter(fast)
    ) {
      writer.write(clipboard);
    }
    final Path absolute = file.toAbsolutePath();
    return absolute.toString();
  }

  public static Clipboard performForwardExtentCopy(final Location[] corners) throws WorldEditException {
    final CuboidRegion region = MapUtils.createRegion(corners);
    final BlockArrayClipboard clipboard = new BlockArrayClipboard(region);
    final com.sk89q.worldedit.world.World world = region.getWorld();
    final WorldEdit instance = WorldEdit.getInstance();
    try (final EditSession session = instance.newEditSession(world)) {
      final BlockVector3 min = region.getMinimumPoint();
      final ForwardExtentCopy forwardExtentCopy = new ForwardExtentCopy(session, region, clipboard, min);
      forwardExtentCopy.setCopyingEntities(true);
      Operations.complete(forwardExtentCopy);
      return clipboard;
    }
  }

  public static CuboidRegion createRegion(final Location[] corners) {
    final Location first = corners[0];
    final Location second = corners[1];
    final World world = requireNonNull(first.getWorld());
    final BlockVector3 firstCorner = BukkitAdapter.asBlockVector(first);
    final BlockVector3 secondCorner = BukkitAdapter.asBlockVector(second);
    final com.sk89q.worldedit.world.World instance = BukkitAdapter.adapt(world);
    return new CuboidRegion(instance, firstCorner, secondCorner);
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
    final World world = requireNonNull(Bukkit.getWorld(worldName));
    final double x = buffer.getDouble();
    final double y = buffer.getDouble();
    final double z = buffer.getDouble();
    final float pitch = (float) buffer.getDouble();
    final float yaw = (float) buffer.getDouble();

    return new Location(world, x, y, z, yaw, pitch);
  }
}
