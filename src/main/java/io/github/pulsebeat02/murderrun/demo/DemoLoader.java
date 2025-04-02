/*

MIT License

Copyright (c) 2025 Brandon Li

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
package io.github.pulsebeat02.murderrun.demo;

import static java.util.Objects.requireNonNull;

import com.google.gson.Gson;
import io.github.pulsebeat02.murderrun.MurderRun;
import io.github.pulsebeat02.murderrun.game.arena.Arena;
import io.github.pulsebeat02.murderrun.game.arena.ArenaManager;
import io.github.pulsebeat02.murderrun.game.lobby.Lobby;
import io.github.pulsebeat02.murderrun.game.lobby.LobbyManager;
import io.github.pulsebeat02.murderrun.gson.GsonProvider;
import io.github.pulsebeat02.murderrun.utils.IOUtils;
import io.github.pulsebeat02.murderrun.utils.map.FastChunkGenerator;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardCopyOption;
import net.citizensnpcs.api.CitizensAPI;
import net.citizensnpcs.api.npc.NPC;
import net.citizensnpcs.api.npc.NPCRegistry;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.WorldCreator;
import org.bukkit.entity.Entity;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Player;

public final class DemoLoader {

  private final MurderRun plugin;
  private final DemoZipContents demoZipContents;

  public DemoLoader(final MurderRun plugin) {
    this.plugin = plugin;
    this.demoZipContents = new DemoZipContents(plugin);
  }

  public void start() {
    if (this.checkDemoExists()) {
      return;
    }
    try {
      this.demoZipContents.unzipContents();
      this.moveLobbySchematic();
      this.moveArenaSchematic();
      this.moveLobbyFolder();
      this.moveArenaFolder();
      this.loadLobbyWorld();
      this.loadArenaWorld();
      this.loadLobbyIntoConfig();
      this.loadArenaIntoConfig();
      this.spawnNPCs();
      this.saveConfigs();
      this.demoZipContents.deleteFolder();
    } catch (final IOException e) {
      throw new AssertionError(e);
    }
  }

  private void spawnNPCs() {
    final NPCRegistry registry = CitizensAPI.getNPCRegistry();
    final NPC npc = registry.createNPC(EntityType.PLAYER, "Fake NPC");
    final World world = requireNonNull(Bukkit.getWorld("LobbyTestWorld"));
    final Location spawnLocation = new Location(world, 0, 0, 0);
    npc.spawn(spawnLocation);

    final Entity entity = npc.getEntity();
    if (entity instanceof final Player player) {
      this.handleSurvivorNPCs(player, world);
      this.handleKillerNPCs(player, world);
    }

    npc.despawn();
    npc.destroy();
  }

  private void handleKillerNPCs(final Player player, final World world) {
    // -964 91 -43
    // -962 91 -43
    final Location first = new Location(world, -964, 91, -43);
    final Location second = new Location(world, -962, 91, -43);
    player.teleport(first);
    player.performCommand("murder npc spawn gadget killer");
    player.teleport(second);
    player.performCommand("murder npc spawn ability killer");
  }

  private void handleSurvivorNPCs(final Player player, final World world) {
    // -964 91 -48
    // -962 91 -48
    final Location first = new Location(world, -964, 91, -48);
    final Location second = new Location(world, -962, 91, -48);
    player.teleport(first);
    player.performCommand("murder npc spawn gadget survivor");
    player.teleport(second);
    player.performCommand("murder npc spawn ability survivor");
  }

  private boolean checkDemoExists() {
    final World arenaWorld = Bukkit.getWorld("ArenaTestWorld");
    final World lobbyWorld = Bukkit.getWorld("LobbyTestWorld");
    return arenaWorld != null && lobbyWorld != null;
  }

  public void saveConfigs() {
    this.plugin.updatePluginData();
  }

  public void loadArenaIntoConfig() throws IOException {
    final Path json = this.demoZipContents.getArenaJson();
    final String raw = Files.readString(json);
    final Gson gson = GsonProvider.getGson();
    final Arena arena = gson.fromJson(raw, Arena.class);
    final ArenaManager arenaManager = this.plugin.getArenaManager();
    arenaManager.addInternalArena(arena);
  }

  public void loadLobbyIntoConfig() throws IOException {
    final Path json = this.demoZipContents.getLobbyJson();
    final String raw = Files.readString(json);
    final Gson gson = GsonProvider.getGson();
    final Lobby lobby = gson.fromJson(raw, Lobby.class);
    final LobbyManager lobbyManager = this.plugin.getLobbyManager();
    lobbyManager.addInternalLobby(lobby);
  }

  public void moveArenaSchematic() throws IOException {
    final Path folder = IOUtils.getPluginDataFolderPath();
    final Path schematics = folder.resolve("schematics");
    final Path arenas = schematics.resolve("arenas");
    final Path src = this.demoZipContents.getArenaTestSchematic();
    final String fileName = IOUtils.getName(src);
    final Path dest = arenas.resolve(fileName);
    Files.createDirectories(arenas);
    Files.move(src, dest, StandardCopyOption.REPLACE_EXISTING);
  }

  public void moveLobbySchematic() throws IOException {
    final Path folder = IOUtils.getPluginDataFolderPath();
    final Path schematics = folder.resolve("schematics");
    final Path lobbies = schematics.resolve("lobbies");
    final Path src = this.demoZipContents.getLobbyTestSchematic();
    final String fileName = IOUtils.getName(src);
    final Path dest = lobbies.resolve(fileName);
    Files.createDirectories(lobbies);
    Files.move(src, dest, StandardCopyOption.REPLACE_EXISTING);
  }

  public void moveArenaFolder() throws IOException {
    final Path folder = IOUtils.getPluginDataFolderPath();
    final Path plugins = requireNonNull(folder.getParent());
    final Path server = requireNonNull(plugins.getParent());
    final Path src = this.demoZipContents.getArenaFolder();
    final String fileName = IOUtils.getName(src);
    final Path dest = server.resolve(fileName);
    Files.move(src, dest, StandardCopyOption.REPLACE_EXISTING);
  }

  public void moveLobbyFolder() throws IOException {
    final Path folder = IOUtils.getPluginDataFolderPath();
    final Path plugins = requireNonNull(folder.getParent());
    final Path server = requireNonNull(plugins.getParent());
    final Path src = this.demoZipContents.getLobbyFolder();
    final String fileName = IOUtils.getName(src);
    final Path dest = server.resolve(fileName);
    Files.move(src, dest, StandardCopyOption.REPLACE_EXISTING);
  }

  public void loadArenaWorld() {
    final FastChunkGenerator generator = new FastChunkGenerator();
    final WorldCreator creator = new WorldCreator("arena");
    creator.keepSpawnInMemory(false);
    creator.generator(generator);
    Bukkit.createWorld(creator);
  }

  public void loadLobbyWorld() {
    final FastChunkGenerator generator = new FastChunkGenerator();
    final WorldCreator creator = new WorldCreator("lobby");
    creator.keepSpawnInMemory(false);
    creator.generator(generator);
    Bukkit.createWorld(creator);
  }
}
