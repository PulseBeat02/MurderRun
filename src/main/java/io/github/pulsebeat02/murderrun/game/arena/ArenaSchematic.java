package io.github.pulsebeat02.murderrun.game.arena;

import static java.util.Objects.requireNonNull;

import com.sk89q.worldedit.EditSession;
import com.sk89q.worldedit.WorldEdit;
import com.sk89q.worldedit.WorldEditException;
import com.sk89q.worldedit.bukkit.BukkitAdapter;
import com.sk89q.worldedit.extent.clipboard.BlockArrayClipboard;
import com.sk89q.worldedit.extent.clipboard.Clipboard;
import com.sk89q.worldedit.extent.clipboard.io.BuiltInClipboardFormat;
import com.sk89q.worldedit.extent.clipboard.io.ClipboardWriter;
import com.sk89q.worldedit.function.operation.ForwardExtentCopy;
import com.sk89q.worldedit.function.operation.Operations;
import com.sk89q.worldedit.math.BlockVector3;
import com.sk89q.worldedit.regions.CuboidRegion;
import io.github.pulsebeat02.murderrun.hibernate.converters.BlockVectorConverter;
import io.github.pulsebeat02.murderrun.hibernate.converters.PathConverter;
import io.github.pulsebeat02.murderrun.utils.IOUtils;
import it.unimi.dsi.fastutil.io.FastBufferedOutputStream;
import jakarta.persistence.Column;
import jakarta.persistence.Convert;
import java.io.IOException;
import java.io.OutputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import org.bukkit.Location;
import org.bukkit.World;

public final class ArenaSchematic {

  @Convert(converter = PathConverter.class)
  @Column(name = "schematic_path")
  private final Path schematicPath;

  @Convert(converter = BlockVectorConverter.class)
  @Column(name = "origin")
  private final BlockVector3 origin;

  public ArenaSchematic(final Path schematicPath, final BlockVector3 origin) {
    this.schematicPath = schematicPath;
    this.origin = origin;
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

    final Path data = IOUtils.getPluginDataFolderPath();
    final Path parent = data.resolve("schematics");
    IOUtils.createFolder(parent);

    final Path file = parent.resolve(name);
    final BuiltInClipboardFormat format = BuiltInClipboardFormat.SPONGE_V3_SCHEMATIC;
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
    final BlockVector3 firstCorner = BukkitAdapter.asBlockVector(first);
    final BlockVector3 secondCorner = BukkitAdapter.asBlockVector(second);
    final com.sk89q.worldedit.world.World instance = BukkitAdapter.adapt(world);
    return new CuboidRegion(instance, firstCorner, secondCorner);
  }

  public Path getSchematicPath() {
    return this.schematicPath;
  }

  public BlockVector3 getOrigin() {
    return this.origin;
  }
}
