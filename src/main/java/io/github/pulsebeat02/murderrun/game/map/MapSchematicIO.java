package io.github.pulsebeat02.murderrun.game.map;

import static java.util.Objects.requireNonNull;

import com.sk89q.worldedit.EditSession;
import com.sk89q.worldedit.WorldEdit;
import com.sk89q.worldedit.WorldEditException;
import com.sk89q.worldedit.bukkit.BukkitAdapter;
import com.sk89q.worldedit.extent.clipboard.Clipboard;
import com.sk89q.worldedit.extent.clipboard.io.ClipboardFormat;
import com.sk89q.worldedit.extent.clipboard.io.ClipboardFormats;
import com.sk89q.worldedit.extent.clipboard.io.ClipboardReader;
import com.sk89q.worldedit.function.operation.Operation;
import com.sk89q.worldedit.function.operation.Operations;
import com.sk89q.worldedit.math.BlockVector3;
import com.sk89q.worldedit.session.ClipboardHolder;
import com.sk89q.worldedit.session.PasteBuilder;
import io.github.pulsebeat02.murderrun.MurderRun;
import io.github.pulsebeat02.murderrun.game.Capabilities;
import io.github.pulsebeat02.murderrun.game.Game;
import io.github.pulsebeat02.murderrun.game.GameSettings;
import io.github.pulsebeat02.murderrun.game.arena.Arena;
import io.github.pulsebeat02.murderrun.game.arena.ArenaSchematic;
import io.github.pulsebeat02.murderrun.game.worldedit.WESpreader;
import io.github.pulsebeat02.murderrun.immutable.SerializableVector;
import it.unimi.dsi.fastutil.io.FastBufferedInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import org.bukkit.Location;
import org.bukkit.World;

public final class MapSchematicIO {

  private static final String WE_SPREADER = "worldedit.spreader.enabled";

  private final Map map;

  public MapSchematicIO(final Map map) {
    this.map = map;
  }

  public void resetMap() {
    try {
      this.enableExtent();
      final Game game = this.map.getGame();
      final GameSettings settings = game.getSettings();
      final Arena arena = requireNonNull(settings.getArena());
      final ArenaSchematic schematic = arena.getSchematic();
      final SerializableVector vector3 = schematic.getOrigin();
      final Clipboard clipboard = this.loadSchematic(schematic);
      final com.sk89q.worldedit.world.World world = this.getWorld();
      final WorldEdit instance = WorldEdit.getInstance();
      this.performResetPaste(instance, world, clipboard, vector3);
    } catch (final WorldEditException | IOException e) {
      throw new AssertionError(e);
    }
  }

  private void enableExtent() {
    final String property = System.getProperty(WE_SPREADER);
    final boolean enabled = Boolean.parseBoolean(property);
    if (Capabilities.FAWE.isDisabled() && !enabled) {
      System.setProperty(WE_SPREADER, "true");
      final Game game = this.map.getGame();
      final MurderRun plugin = game.getPlugin();
      final WESpreader spreader = new WESpreader(plugin);
      spreader.load();
    }
  }

  private com.sk89q.worldedit.world.World getWorld() {
    final Game game = this.map.getGame();
    final GameSettings settings = game.getSettings();
    final Arena arena = requireNonNull(settings.getArena());
    final Location location = arena.getSpawn();
    final World world = requireNonNull(location.getWorld());
    return BukkitAdapter.adapt(world);
  }

  private void performResetPaste(
    final WorldEdit instance,
    final com.sk89q.worldedit.world.World world,
    final Clipboard clipboard,
    final SerializableVector vector3
  ) throws WorldEditException {
    try (final EditSession session = instance.newEditSession(world)) {
      final ClipboardHolder holder = new ClipboardHolder(clipboard);
      final BlockVector3 internal = vector3.getVector3();
      final PasteBuilder extent = holder.createPaste(session).to(internal).ignoreAirBlocks(false);
      final Operation operation = extent.build();
      Operations.complete(operation);
    }
  }

  private Clipboard loadSchematic(final ArenaSchematic schematic) throws IOException {
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
}
