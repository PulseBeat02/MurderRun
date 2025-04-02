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
package io.github.pulsebeat02.murderrun.game.map;

import static java.util.Objects.requireNonNull;

import com.sk89q.worldedit.WorldEdit;
import com.sk89q.worldedit.WorldEditException;
import com.sk89q.worldedit.bukkit.BukkitAdapter;
import com.sk89q.worldedit.extent.clipboard.Clipboard;
import io.github.pulsebeat02.murderrun.game.GameSettings;
import io.github.pulsebeat02.murderrun.game.arena.Arena;
import io.github.pulsebeat02.murderrun.game.lobby.Lobby;
import io.github.pulsebeat02.murderrun.immutable.SerializableVector;
import io.github.pulsebeat02.murderrun.utils.IOUtils;
import io.github.pulsebeat02.murderrun.utils.MapUtils;
import java.io.IOException;
import java.nio.file.Path;
import java.util.Collection;
import java.util.HashSet;
import java.util.UUID;
import net.citizensnpcs.api.CitizensAPI;
import net.citizensnpcs.api.npc.NPC;
import net.citizensnpcs.api.npc.NPCRegistry;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.event.player.PlayerTeleportEvent;
import org.bukkit.util.BoundingBox;
import org.bukkit.util.Vector;

public final class MapSchematicIO {

  private final GameSettings settings;
  private final UUID uuid;
  private final Collection<NPC> npcs;

  public MapSchematicIO(final GameSettings settings, final UUID uuid) {
    this.settings = settings;
    this.uuid = uuid;
    this.npcs = new HashSet<>();
  }

  public void resetMap() {
    this.removeCitizensNPCs();
    this.unloadWorld();
    this.deleteWorld();
  }

  private void deleteWorld() {
    final String name = this.uuid.toString();
    final Path path = IOUtils.getPluginDataFolderPath();
    final Path pluginParent = requireNonNull(path.getParent());
    final Path moreParent = requireNonNull(pluginParent.getParent());
    final Path world = moreParent.resolve(name);
    IOUtils.deleteExistingDirectory(world);
  }

  private void unloadWorld() {
    final String name = this.uuid.toString();
    final World world = requireNonNull(Bukkit.getWorld(name));
    Bukkit.unloadWorld(world, false);
  }

  private World createWorld() {
    final String name = this.uuid.toString();
    final Arena arena = requireNonNull(this.settings.getArena());
    final Lobby lobby = requireNonNull(this.settings.getLobby());
    final Location previous = arena.getSpawn();
    final World previousWorld = requireNonNull(previous.getWorld());
    final World world = MapUtils.createVoidWorld(name, previousWorld);
    arena.relativizeLocations(this.uuid);
    lobby.relativizeLocations(this.uuid);
    return world;
  }

  public void pasteMap() {
    MapUtils.enableExtent();
    try {
      this.copyCitizensNPCs();
      this.createWorld();
      this.pasteLobbySchematic();
      this.pasteArenaSchematic();
      this.pasteCitizensNPCs();
    } catch (final WorldEditException | IOException e) {
      throw new AssertionError(e);
    }
  }

  private void removeCitizensNPCs() {
    for (final NPC npc : this.npcs) {
      npc.despawn();
      npc.destroy();
    }
  }

  private void pasteCitizensNPCs() {
    final String name = this.uuid.toString();
    final World world = requireNonNull(Bukkit.getWorld(name));
    for (final NPC npc : this.npcs) {
      final Location location = npc.getStoredLocation();
      final Location clone = location.clone();
      clone.setWorld(world);
      npc.teleport(clone, PlayerTeleportEvent.TeleportCause.PLUGIN);
    }
  }

  private void copyCitizensNPCs() {
    final NPCRegistry registry = CitizensAPI.getNPCRegistry();
    final Lobby lobby = requireNonNull(this.settings.getLobby());
    final Location[] corners = lobby.getCorners();
    final Location first = corners[0];
    final Location second = corners[1];
    final BoundingBox boundingBox = BoundingBox.of(first, second);
    final World realWorld = requireNonNull(first.getWorld());
    final String realName = realWorld.getName();
    for (final NPC npc : registry) {
      final Location location = npc.getStoredLocation();
      if (location == null) {
        continue;
      }
      final World world = requireNonNull(location.getWorld());
      final String name = world.getName();
      if (!realName.equals(name)) {
        continue;
      }
      final Vector vector = location.toVector();
      if (boundingBox.contains(vector)) {
        final NPC copy = npc.clone();
        this.npcs.add(copy);
      }
    }
  }

  private void pasteArenaSchematic() throws WorldEditException, IOException {
    final Arena arena = requireNonNull(this.settings.getArena());
    final Schematic schematic = arena.getSchematic();
    this.pasteSchematic(schematic);
  }

  private void pasteLobbySchematic() throws WorldEditException, IOException {
    final Lobby lobby = requireNonNull(this.settings.getLobby());
    final Schematic schematic = lobby.getSchematic();
    this.pasteSchematic(schematic);
  }

  private void pasteSchematic(final Schematic schematic) throws WorldEditException {
    final SerializableVector vector3 = schematic.getOrigin();
    final Clipboard clipboard = schematic.getClipboard();
    final com.sk89q.worldedit.world.World world = this.getWorld();
    final WorldEdit instance = WorldEdit.getInstance();
    MapUtils.performPaste(instance, world, clipboard, vector3);
  }

  private com.sk89q.worldedit.world.World getWorld() {
    final Arena arena = requireNonNull(this.settings.getArena());
    final Location location = arena.getSpawn();
    final World world = requireNonNull(location.getWorld());
    return BukkitAdapter.adapt(world);
  }

  public GameSettings getSettings() {
    return this.settings;
  }

  public UUID getUuid() {
    return this.uuid;
  }

  public Collection<NPC> getNpcs() {
    return this.npcs;
  }
}
