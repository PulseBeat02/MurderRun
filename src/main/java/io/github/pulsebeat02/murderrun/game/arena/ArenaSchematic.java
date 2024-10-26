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
import io.github.pulsebeat02.murderrun.data.hibernate.converters.SerializableVectorConverter;
import io.github.pulsebeat02.murderrun.immutable.SerializableVector;
import io.github.pulsebeat02.murderrun.utils.IOUtils;
import it.unimi.dsi.fastutil.io.FastBufferedOutputStream;
import jakarta.persistence.Column;
import jakarta.persistence.Convert;
import java.io.IOException;
import java.io.OutputStream;
import java.io.Serial;
import java.io.Serializable;
import java.nio.file.Files;
import java.nio.file.Path;
import org.bukkit.Location;
import org.bukkit.World;

public final class ArenaSchematic implements Serializable {

  @Serial
  private static final long serialVersionUID = 4953428050756665476L;

  @Column(name = "schematic_path")
  private final String schematicPath;

  @Convert(converter = SerializableVectorConverter.class)
  @Column(name = "origin")
  private final SerializableVector origin;

  public ArenaSchematic(final String schematicPath, final SerializableVector origin) {
    this.schematicPath = schematicPath;
    this.origin = origin;
  }

  public static ArenaSchematic copyAndCreateSchematic(final String name, final Location[] corners) {
    try {
      final Clipboard clipboard = performForwardExtentCopy(corners);
      final String path = performSchematicWrite(clipboard, name);
      final BlockVector3 origin = clipboard.getOrigin();
      final SerializableVector serializable = new SerializableVector(origin);
      return new ArenaSchematic(path, serializable);
    } catch (final WorldEditException | IOException e) {
      throw new AssertionError(e);
    }
  }

  private static Clipboard performForwardExtentCopy(final Location[] corners) throws WorldEditException {
    final CuboidRegion region = createRegion(corners);
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

  private static String performSchematicWrite(final Clipboard clipboard, final String name) throws IOException {
    final Path data = IOUtils.getPluginDataFolderPath();
    final Path parent = data.resolve("schematics");
    IOUtils.createFolder(parent);

    final Path file = parent.resolve(name);
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

  private static CuboidRegion createRegion(final Location[] corners) {
    final Location first = corners[0];
    final Location second = corners[1];
    final World world = requireNonNull(first.getWorld());
    final BlockVector3 firstCorner = BukkitAdapter.asBlockVector(first);
    final BlockVector3 secondCorner = BukkitAdapter.asBlockVector(second);
    final com.sk89q.worldedit.world.World instance = BukkitAdapter.adapt(world);
    return new CuboidRegion(instance, firstCorner, secondCorner);
  }

  public String getSchematicPath() {
    return this.schematicPath;
  }

  public SerializableVector getOrigin() {
    return this.origin;
  }
}
