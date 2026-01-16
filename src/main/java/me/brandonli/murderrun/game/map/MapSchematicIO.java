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
package me.brandonli.murderrun.game.map;

import static java.util.Objects.requireNonNull;

import com.sk89q.worldedit.bukkit.BukkitAdapter;
import com.sk89q.worldedit.extent.clipboard.Clipboard;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Collection;
import java.util.HashSet;
import java.util.UUID;
import java.util.concurrent.CompletableFuture;
import me.brandonli.murderrun.MurderRun;
import me.brandonli.murderrun.game.GameSettings;
import me.brandonli.murderrun.game.arena.Arena;
import me.brandonli.murderrun.game.lobby.Lobby;
import me.brandonli.murderrun.game.lobby.PreGameManager;
import me.brandonli.murderrun.utils.IOUtils;
import me.brandonli.murderrun.utils.immutable.SerializableVector;
import me.brandonli.murderrun.utils.map.MapUtils;
import net.citizensnpcs.api.CitizensAPI;
import net.citizensnpcs.api.npc.NPC;
import net.citizensnpcs.api.npc.NPCRegistry;
import org.bukkit.Bukkit;
import org.bukkit.Chunk;
import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.block.Block;
import org.bukkit.event.player.PlayerTeleportEvent;
import org.bukkit.scheduler.BukkitRunnable;
import org.bukkit.util.BoundingBox;
import org.bukkit.util.Vector;
import org.checkerframework.checker.nullness.qual.Nullable;

public final class MapSchematicIO {

  private final GameSettings settings;
  private final UUID uuid;
  private final Collection<NPC> npcs;
  private final PreGameManager manager;
  private final Collection<Chunk> loaded;

  private @Nullable Thread shutdownHook;

  public MapSchematicIO(final PreGameManager manager, final GameSettings settings, final UUID uuid) {
    this.manager = manager;
    this.settings = settings;
    this.uuid = uuid;
    this.npcs = new HashSet<>();
    this.loaded = new HashSet<>();
  }

  public void resetMap() {
    this.removeCitizensNPCs();
    this.unloadChunks();
    this.unloadWorld();
    this.deleteWorldAsync();
    this.removeShutdownHook();
  }

  // special reset for shutdown
  // shutdown hooks are best-effort and may not run in all scenarios (e.g., crash, kill -9)
  public void resetMapShutdown() {
    this.removeCitizensNPCs();
    this.addShutdownHook();
  }

  private void addShutdownHook() {
    final Thread thread = new Thread(this::deleteWorld);
    final Runtime runtime = Runtime.getRuntime();
    runtime.addShutdownHook(thread);
    this.shutdownHook = thread;
  }

  private void removeShutdownHook() {
    if (this.shutdownHook != null) {
      try {
        final Runtime runtime = Runtime.getRuntime();
        final Thread shutdownHook = requireNonNull(this.shutdownHook); // checker...
        runtime.removeShutdownHook(shutdownHook);
        this.shutdownHook = null;
      } catch (final IllegalStateException ignored) {
        // JVM already shutting down, hook will run naturally
      }
    }
  }

  private void deleteWorld() {
    final String name = this.uuid.toString();
    final Path path = IOUtils.getPluginDataFolderPath();
    final Path pluginParent = requireNonNull(path.getParent());
    final Path moreParent = requireNonNull(pluginParent.getParent());
    final Path world = moreParent.resolve(name);
    if (Files.exists(world)) {
      IOUtils.deleteExistingDirectory(world);
    }
  }

  private void deleteWorldAsync() {
    final String name = this.uuid.toString();
    final Path path = IOUtils.getPluginDataFolderPath();
    final Path pluginParent = requireNonNull(path.getParent());
    final Path moreParent = requireNonNull(pluginParent.getParent());
    final Path world = moreParent.resolve(name);
    CompletableFuture.runAsync(() -> {
      if (Files.exists(world)) {
        IOUtils.deleteExistingDirectory(world);
      }
    });
  }

  private void unloadWorld() {
    final String name = this.uuid.toString();
    final World world = Bukkit.getWorld(name);
    if (world != null) {
      Bukkit.unloadWorld(world, false);
    }
  }

  private World createWorld() {
    final String name = this.uuid.toString();
    final Arena arena = requireNonNull(this.settings.getArena());
    final Lobby lobby = requireNonNull(this.settings.getLobby());
    final Location previous = arena.getSpawn();
    final World previousWorld = requireNonNull(previous.getWorld());
    final World world = MapUtils.createVoidWorld(name, previousWorld);
    world.setAutoSave(false);
    this.settings.setArena(arena.relativizeLocations(this.uuid));
    this.settings.setLobby(lobby.relativizeLocations(this.uuid));
    this.loadArenaChunks();
    this.loadLobbyChunks();
    this.settings.setWorld(world);
    return world;
  }

  private void loadLobbyChunks() {
    final Lobby lobby = requireNonNull(this.settings.getLobby());
    final Location[] corners = lobby.getCorners();
    this.loadChunksBetweenCorners(corners);
  }

  private void loadArenaChunks() {
    final Arena arena = requireNonNull(this.settings.getArena());
    final Location[] corners = arena.getCorners();
    this.loadChunksBetweenCorners(corners);
  }

  private void loadChunksBetweenCorners(final Location[] corners) {
    final Location first = corners[0];
    final Location second = corners[1];
    final int minX = Math.min(first.getBlockX(), second.getBlockX()) >> 4;
    final int maxX = Math.max(first.getBlockX(), second.getBlockX()) >> 4;
    final int minZ = Math.min(first.getBlockZ(), second.getBlockZ()) >> 4;
    final int maxZ = Math.max(first.getBlockZ(), second.getBlockZ()) >> 4;
    final World world = requireNonNull(first.getWorld());
    for (int x = minX; x <= maxX; x++) {
      for (int z = minZ; z <= maxZ; z++) {
        final Chunk chunk = world.getChunkAt(x, z);
        chunk.setForceLoaded(true);
        this.loaded.add(chunk);
      }
    }
  }

  private void unloadChunks() {
    for (final Chunk chunk : this.loaded) {
      chunk.setForceLoaded(false);
    }
    this.loaded.clear();
  }

  public void pasteMap() {
    MapUtils.enableExtent();
    this.copyCitizensNPCs();
    this.createWorld();
    this.pasteLobbySchematic();
    this.pasteArenaSchematic();
    this.pasteCitizensNPCs();
  }

  private void removeCitizensNPCs() {
    for (final NPC npc : this.npcs) {
      npc.despawn();
      npc.destroy();
    }
  }

  private void pasteCitizensNPCs() {
    final MurderRun plugin = this.manager.getPlugin();
    final CitizensPrepareRunnable task = new CitizensPrepareRunnable();
    task.runTaskTimer(plugin, 0L, 20L);
  }

  private class CitizensPrepareRunnable extends BukkitRunnable {

    @Override
    public void run() {
      if (!this.waitUntilReady()) {
        return;
      }
      final String name = MapSchematicIO.this.uuid.toString();
      final World world = requireNonNull(Bukkit.getWorld(name));
      for (final NPC npc : MapSchematicIO.this.npcs) {
        final Location location = npc.getStoredLocation();
        final Location clone = location.clone();
        clone.setWorld(world);
        clone.add(0, 3, 0);
        npc.teleport(clone, PlayerTeleportEvent.TeleportCause.PLUGIN);
      }
      this.cancel();
    }

    private boolean waitUntilReady() {
      final GameSettings settings = MapSchematicIO.this.manager.getSettings();
      final Lobby lobby = requireNonNull(settings.getLobby());
      final Location spawn = lobby.getLobbySpawn();
      final Block block = spawn.getBlock();
      final int x = block.getX();
      final int z = block.getZ();
      final World world = block.getWorld();
      final Block under = world.getHighestBlockAt(x, z);
      return under.isSolid(); // any block will do as long as it's solid, showing it loaded
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
        final Chunk chunk = location.getChunk();
        chunk.load();
        final NPC copy = npc.clone();
        this.npcs.add(copy);
      }
    }
  }

  private CompletableFuture<Void> pasteArenaSchematic() {
    final Arena arena = requireNonNull(this.settings.getArena());
    final Schematic schematic = arena.getSchematic();
    return this.pasteSchematic(schematic);
  }

  private CompletableFuture<Void> pasteLobbySchematic() {
    final Lobby lobby = requireNonNull(this.settings.getLobby());
    final Schematic schematic = lobby.getSchematic();
    return this.pasteSchematic(schematic);
  }

  private CompletableFuture<Void> pasteSchematic(final Schematic schematic) {
    final SerializableVector vector3 = schematic.getOrigin();
    final Clipboard clipboard = schematic.getClipboard();
    final com.sk89q.worldedit.world.World world = this.getWorld();
    return MapUtils.performPaste(world, clipboard, vector3);
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
