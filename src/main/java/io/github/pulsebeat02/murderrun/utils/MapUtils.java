package io.github.pulsebeat02.murderrun.utils;

import static java.util.Objects.requireNonNull;

import com.sk89q.worldedit.EditSession;
import com.sk89q.worldedit.WorldEdit;
import com.sk89q.worldedit.WorldEditException;
import com.sk89q.worldedit.bukkit.BukkitAdapter;
import com.sk89q.worldedit.extent.clipboard.BlockArrayClipboard;
import com.sk89q.worldedit.extent.clipboard.Clipboard;
import com.sk89q.worldedit.extent.clipboard.io.BuiltInClipboardFormat;
import com.sk89q.worldedit.extent.clipboard.io.ClipboardFormat;
import com.sk89q.worldedit.extent.clipboard.io.ClipboardFormats;
import com.sk89q.worldedit.extent.clipboard.io.ClipboardReader;
import com.sk89q.worldedit.extent.clipboard.io.ClipboardWriter;
import com.sk89q.worldedit.function.operation.ForwardExtentCopy;
import com.sk89q.worldedit.function.operation.Operation;
import com.sk89q.worldedit.function.operation.Operations;
import com.sk89q.worldedit.math.BlockVector3;
import com.sk89q.worldedit.regions.CuboidRegion;
import com.sk89q.worldedit.regions.Region;
import com.sk89q.worldedit.session.ClipboardHolder;
import com.sk89q.worldedit.session.PasteBuilder;
import io.github.pulsebeat02.murderrun.game.Game;
import io.github.pulsebeat02.murderrun.game.GameSettings;
import io.github.pulsebeat02.murderrun.game.arena.Arena;
import io.github.pulsebeat02.murderrun.game.arena.ArenaSchematic;
import io.github.pulsebeat02.murderrun.game.map.Map;
import it.unimi.dsi.fastutil.io.FastBufferedInputStream;
import it.unimi.dsi.fastutil.io.FastBufferedOutputStream;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.plugin.Plugin;
import org.bukkit.util.EulerAngle;

public final class MapUtils {

  private static final Path PARENT_FOLDER;

  static {
    final Plugin plugin =
        CursedPluginInstanceRetrieverOnlyForUtilityClassesProvider.retrievePluginInstance();
    final File folder = plugin.getDataFolder();
    PARENT_FOLDER = folder.toPath();
  }

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

  public static void resetMap(final Map map) {
    try {
      final Game game = map.getGame();
      final GameSettings settings = game.getSettings();
      final Arena arena = requireNonNull(settings.getArena());
      final ArenaSchematic schematic = arena.getSchematic();
      final BlockVector3 vector3 = schematic.getOrigin();
      final Clipboard clipboard = loadSchematic(schematic);
      final Region region = clipboard.getRegion();
      final com.sk89q.worldedit.world.World world = region.getWorld();
      final WorldEdit instance = WorldEdit.getInstance();
      performResetPaste(instance, world, clipboard, vector3);
    } catch (final WorldEditException | IOException e) {
      throw new AssertionError(e);
    }
  }

  private static void performResetPaste(
      final WorldEdit instance,
      final com.sk89q.worldedit.world.World world,
      final Clipboard clipboard,
      final BlockVector3 vector3)
      throws WorldEditException {
    try (final EditSession session = instance.newEditSession(world)) {
      final ClipboardHolder holder = new ClipboardHolder(clipboard);
      final PasteBuilder extent = holder.createPaste(session).to(vector3).ignoreAirBlocks(false);
      final Operation operation = extent.build();
      Operations.complete(operation);
    }
  }

  private static Clipboard loadSchematic(final ArenaSchematic schematic) throws IOException {
    final Path path = schematic.getSchematicPath();
    final File legacyPath = path.toFile();
    final ClipboardFormat format = requireNonNull(ClipboardFormats.findByFile(legacyPath));
    try (final InputStream stream = Files.newInputStream(path);
        final FastBufferedInputStream fast = new FastBufferedInputStream(stream);
        final ClipboardReader reader = format.getReader(fast)) {
      return reader.read();
    }
  }

  public static ArenaSchematic copyAndCreateSchematic(final String name, final Location[] corners) {
    try {
      final Clipboard clipboard = performForwardExtentCopy(corners);
      final Path path = performSchematicWrite(clipboard, name);
      final BlockVector3 origin = clipboard.getOrigin();
      return new ArenaSchematic(path, origin);
    } catch (final WorldEditException | IOException e) {
      throw new AssertionError(e);
    }
  }

  private static Clipboard performForwardExtentCopy(final Location[] corners)
      throws WorldEditException {
    final CuboidRegion region = createRegion(corners);
    final BlockArrayClipboard clipboard = new BlockArrayClipboard(region);
    final com.sk89q.worldedit.world.World world = region.getWorld();
    final WorldEdit instance = WorldEdit.getInstance();
    try (final EditSession session = instance.newEditSession(world)) {
      final BlockVector3 min = region.getMinimumPoint();
      final ForwardExtentCopy forwardExtentCopy =
          new ForwardExtentCopy(session, region, clipboard, min);
      forwardExtentCopy.setCopyingEntities(true);
      Operations.complete(forwardExtentCopy);
      return clipboard;
    }
  }

  private static Path performSchematicWrite(final Clipboard clipboard, final String name)
      throws IOException {
    final Path file = PARENT_FOLDER.resolve(name);
    final BuiltInClipboardFormat format = BuiltInClipboardFormat.MCEDIT_SCHEMATIC;
    try (final OutputStream stream = Files.newOutputStream(file);
        final OutputStream fast = new FastBufferedOutputStream(stream);
        final ClipboardWriter writer = format.getWriter(fast)) {
      writer.write(clipboard);
    }
    return file;
  }

  private static CuboidRegion createRegion(final Location[] corners) {
    final Location first = corners[0];
    final Location second = corners[1];
    final World world = requireNonNull(first.getWorld());
    final BlockVector3 firstCorner = toBlockVector3(first);
    final BlockVector3 secondCorner = toBlockVector3(second);
    final com.sk89q.worldedit.world.World instance = BukkitAdapter.adapt(world);
    return new CuboidRegion(instance, firstCorner, secondCorner);
  }

  public static BlockVector3 toBlockVector3(final Location location) {
    final double x = location.getX();
    final double y = location.getY();
    final double z = location.getZ();
    return BlockVector3.at(x, y, z);
  }
}
