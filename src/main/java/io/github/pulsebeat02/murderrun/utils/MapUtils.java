package io.github.pulsebeat02.murderrun.utils;


import com.sk89q.worldedit.EditSession;
import com.sk89q.worldedit.WorldEdit;
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
import com.sk89q.worldedit.session.ClipboardHolder;
import io.github.pulsebeat02.murderrun.arena.MurderArena;
import io.github.pulsebeat02.murderrun.arena.MurderArenaSchematic;
import io.github.pulsebeat02.murderrun.game.MurderGame;
import io.github.pulsebeat02.murderrun.game.MurderSettings;
import io.github.pulsebeat02.murderrun.map.MurderMap;
import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.plugin.Plugin;
import org.bukkit.plugin.PluginManager;
import org.bukkit.util.EulerAngle;

public final class MapUtils {

  private static final Path PARENT_FOLDER;

  static {
    final PluginManager manager = Bukkit.getPluginManager();
    final Plugin plugin = manager.getPlugin("MurderRun");
    if (plugin == null) {
      throw new AssertionError("Unable to retrieve plugin class!");
    }
    final File folder = plugin.getDataFolder();
    PARENT_FOLDER = folder.toPath();
  }

  private MapUtils() {
    throw new UnsupportedOperationException("Utility class cannot be instantiated");
  }

  public static EulerAngle toEulerAngle(final int x, final int y, final int z) {
    return new EulerAngle(Math.toRadians(x), Math.toRadians(y), Math.toRadians(z));
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

  public static void resetMap(final MurderMap map) {
    final MurderGame game = map.getGame();
    final MurderSettings settings = game.getSettings();
    final MurderArena arena = settings.getArena();
    final MurderArenaSchematic schematic = arena.getSchematic();
    final BlockVector3 vector3 = schematic.getOrigin();
    try (final Clipboard clipboard = loadSchematic(schematic)) {
      final Region region = clipboard.getRegion();
      final com.sk89q.worldedit.world.World world = region.getWorld();
      try (final EditSession session = WorldEdit.getInstance().newEditSession(world)) {
        final Operation operation =
            new ClipboardHolder(clipboard)
                .createPaste(session)
                .to(vector3)
                .ignoreAirBlocks(false)
                .build();
        Operations.complete(operation);
      }
    }
  }

  private static Clipboard loadSchematic(final MurderArenaSchematic schematic) {
    final Path path = schematic.getSchematicPath();
    final ClipboardFormat format = ClipboardFormats.findByFile(path.toFile());
    if (format == null) {
      throw new AssertionError(String.format("Schematic %s is corrupted or doesn't exist!", path));
    }
    try (final ClipboardReader reader = format.getReader(Files.newInputStream(path))) {
      return reader.read();
    } catch (final IOException e) {
      throw new AssertionError(e);
    }
  }

  public static MurderArenaSchematic copyAndCreateSchematic(
      final String name, final Location[] corners) {
    try (final Clipboard clipboard = performForwardExtentCopy(corners)) {
      final Path path = performSchematicWrite(clipboard, name);
      final BlockVector3 origin = clipboard.getOrigin();
      return new MurderArenaSchematic(path, origin);
    }
  }

  private static Clipboard performForwardExtentCopy(final Location[] corners) {
    final CuboidRegion region = createRegion(corners);
    final BlockArrayClipboard clipboard = new BlockArrayClipboard(region);
    final com.sk89q.worldedit.world.World world = region.getWorld();
    try (final EditSession session = WorldEdit.getInstance().newEditSession(world)) {
      final ForwardExtentCopy forwardExtentCopy =
          new ForwardExtentCopy(session, region, clipboard, region.getMinimumPoint());
      forwardExtentCopy.setCopyingEntities(true);
      Operations.complete(forwardExtentCopy);
      return clipboard;
    }
  }

  private static Path performSchematicWrite(final Clipboard clipboard, final String name) {
    final Path file = PARENT_FOLDER.resolve(name);
    try (final ClipboardWriter writer =
        BuiltInClipboardFormat.FAST.getWriter(Files.newOutputStream(file))) {
      writer.write(clipboard);
    } catch (final IOException e) {
      throw new AssertionError(e);
    }
    return file;
  }

  private static CuboidRegion createRegion(final Location[] corners) {
    final Location first = corners[0];
    final Location second = corners[1];
    final World world = first.getWorld();
    if (world == null) {
      throw new AssertionError("Location doesn't have World attached to it!");
    }
    final BlockVector3 firstCorner = toBlockVector3(first);
    final BlockVector3 secondCorner = toBlockVector3(second);
    final com.sk89q.worldedit.world.World instance = BukkitAdapter.adapt(world);
    return new CuboidRegion(instance, firstCorner, secondCorner);
  }

  private static BlockVector3 toBlockVector3(final Location location) {
    final double x = location.getX();
    final double y = location.getY();
    final double z = location.getZ();
    return BlockVector3.at(x, y, z);
  }
}
