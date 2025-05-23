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
package me.brandonli.murderrun.commmand;

import static java.util.Objects.requireNonNull;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.concurrent.CompletableFuture;
import me.brandonli.murderrun.MurderRun;
import me.brandonli.murderrun.game.arena.Arena;
import me.brandonli.murderrun.game.arena.ArenaManager;
import me.brandonli.murderrun.game.arena.drops.TerrainDropAnalyzer;
import me.brandonli.murderrun.locale.AudienceProvider;
import me.brandonli.murderrun.locale.Message;
import me.brandonli.murderrun.utils.ComponentUtils;
import me.brandonli.murderrun.utils.map.MapUtils;
import net.kyori.adventure.audience.Audience;
import net.kyori.adventure.platform.bukkit.BukkitAudiences;
import net.kyori.adventure.text.Component;
import org.bukkit.Location;
import org.bukkit.block.Block;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.incendo.cloud.annotation.specifier.Quoted;
import org.incendo.cloud.annotations.AnnotationParser;
import org.incendo.cloud.annotations.Command;
import org.incendo.cloud.annotations.CommandDescription;
import org.incendo.cloud.annotations.Permission;

public final class ArenaCommand implements AnnotationCommandFeature {

  private MurderRun plugin;
  private BukkitAudiences audiences;

  private String name;
  private Location spawn;
  private Location truck;
  private Location first;
  private Location second;
  private Collection<Location> itemLocations;

  @Override
  public void registerFeature(final MurderRun plugin, final AnnotationParser<CommandSender> parser) {
    final AudienceProvider handler = plugin.getAudience();
    this.audiences = handler.retrieve();
    this.plugin = plugin;
    this.itemLocations = Collections.synchronizedSet(new HashSet<>());
  }

  @Permission("murderrun.command.arena.copy")
  @CommandDescription("murderrun.command.arena.copy.info")
  @Command(value = "murder arena copy <name>", requiredSender = Player.class)
  public void copyArenaSettings(final Player sender, @Quoted final String name) {
    final Audience audience = this.audiences.player(sender);
    if (this.checkInvalidArena(audience, name)) {
      return;
    }

    final ArenaManager manager = this.plugin.getArenaManager();
    final Map<String, Arena> arenas = manager.getArenas();
    final Arena arena = requireNonNull(arenas.get(name));
    final List<Location> locations = Arrays.asList(arena.getCarPartLocations());
    this.name = arena.getName();
    this.spawn = arena.getSpawn();
    this.truck = arena.getTruck();
    this.first = arena.getFirstCorner();
    this.second = arena.getSecondCorner();
    this.itemLocations = new ArrayList<>(locations);

    final Component msg = Message.ARENA_COPY.build();
    audience.sendMessage(msg);
  }

  @Permission("murderrun.command.arena.set.item.add")
  @CommandDescription("murderrun.command.arena.set.item.spawn.location.add.info")
  @Command(value = "murder arena set item add", requiredSender = Player.class)
  public void addItemLocation(final Player sender) {
    final Location location = sender.getLocation();
    this.addItemLocation(sender, location);
  }

  public void addItemLocation(final Player sender, final Location location) {
    final Block block = location.getBlock();
    final Location blockLoc = block.getLocation();
    this.itemLocations.add(blockLoc);

    final Audience audience = this.audiences.player(sender);
    final Component msg = ComponentUtils.createLocationComponent(Message.ARENA_ITEM_ADD, blockLoc);
    audience.sendMessage(msg);
  }

  @Permission("murderrun.command.arena.set.item.remove")
  @CommandDescription("murderrun.command.arena.set.item.spawn.location.remove.info")
  @Command(value = "murder arena set item remove", requiredSender = Player.class)
  public void removeItemLocation(final Player sender) {
    final Location location = sender.getLocation();
    this.removeItemLocation(sender, location);
  }

  public void removeItemLocation(final Player sender, final Location location) {
    final Block block = location.getBlock();
    final Location blockLoc = block.getLocation();
    final Audience audience = this.audiences.player(sender);
    final Component msg = this.itemLocations.remove(blockLoc)
      ? ComponentUtils.createLocationComponent(Message.ARENA_ITEM_REMOVE, blockLoc)
      : Message.ARENA_ITEM_REMOVE_ERROR.build();
    audience.sendMessage(msg);
  }

  @Permission("murderrun.command.arena.set.item.list")
  @CommandDescription("murderrun.command.arena.set.item.spawn.location.list.info")
  @Command(value = "murder arena set item list", requiredSender = Player.class)
  public void listItemLocations(final Player sender) {
    final List<String> messages = new ArrayList<>();
    for (final Location location : this.itemLocations) {
      final int x = location.getBlockX();
      final int y = location.getBlockY();
      final int z = location.getBlockZ();
      final String raw = "(%s,%s,%s)".formatted(x, y, z);
      messages.add(raw);
    }

    final Audience audience = this.audiences.player(sender);
    final Component msg = Message.ARENA_ITEM_LIST.build(messages);
    audience.sendMessage(msg);
  }

  @Permission("murderrun.command.arena.list")
  @CommandDescription("murderrun.command.arena.list.info")
  @Command(value = "murder arena list", requiredSender = Player.class)
  public void listArenas(final Player sender) {
    final ArenaManager manager = this.plugin.getArenaManager();
    final Map<String, Arena> arenas = manager.getArenas();
    final List<String> keys = new ArrayList<>(arenas.keySet());
    final Audience audience = this.audiences.player(sender);
    final Component message = Message.ARENA_LIST.build(keys);
    audience.sendMessage(message);
  }

  @Permission("murderrun.command.arena.remove")
  @CommandDescription("murderrun.command.arena.remove.info")
  @Command(value = "murder arena remove <name>", requiredSender = Player.class)
  public void removeArena(final Player sender, final String name) {
    final Audience audience = this.audiences.player(sender);
    if (this.checkInvalidArena(audience, name)) {
      return;
    }

    final ArenaManager manager = this.plugin.getArenaManager();
    manager.removeArena(name);

    final Component message = Message.ARENA_REMOVE.build(name);
    audience.sendMessage(message);
  }

  private boolean checkInvalidArena(final Audience audience, final String name) {
    final ArenaManager manager = this.plugin.getArenaManager();
    final Map<String, Arena> arenas = manager.getArenas();
    final Arena arena = arenas.get(name);
    if (arena == null) {
      final Component message = Message.ARENA_REMOVE_ERROR.build();
      audience.sendMessage(message);
      return true;
    }
    return false;
  }

  @Permission("murderrun.command.arena.create")
  @CommandDescription("murderrun.command.arena.create.info")
  @Command(value = "murder arena create", requiredSender = Player.class)
  public void createArena(final Player sender) {
    final Audience audience = this.audiences.player(sender);
    if (
      this.handleNullCorner(audience) || this.handleNullSpawn(audience) || this.handleNullTruck(audience) || this.handleNullName(audience)
    ) {
      return;
    }

    final Component loadMsg = Message.ARENA_CREATE_LOAD.build();
    audience.sendMessage(loadMsg);

    final Location[] corners = new Location[] { this.first, this.second };
    final Location[] locations = this.itemLocations.toArray(new Location[0]);
    final CompletableFuture<Location[]> future;
    if (locations.length == 0) {
      final TerrainDropAnalyzer analyzer = new TerrainDropAnalyzer(this.plugin, corners, this.spawn);
      future = analyzer.getRandomDrops();
    } else {
      future = CompletableFuture.completedFuture(locations);
    }

    final Location actual = MapUtils.getSafeSpawn(this.spawn);
    future.thenAccept(items -> {
      final ArenaManager manager = this.plugin.getArenaManager();
      manager.addArena(this.name, corners, items, actual, this.truck);
      this.plugin.updatePluginData();

      final Component msg1 = Message.ARENA_BUILT.build();
      audience.sendMessage(msg1);
    });
  }

  private boolean handleNullCorner(final Audience audience) {
    if (this.first == null || this.second == null) {
      final Component message = Message.ARENA_CORNER_ERROR.build();
      audience.sendMessage(message);
      return true;
    }
    return false;
  }

  private boolean handleNullSpawn(final Audience audience) {
    if (this.spawn == null) {
      final Component message = Message.ARENA_SPAWN_ERROR.build();
      audience.sendMessage(message);
      return true;
    }
    return false;
  }

  private boolean handleNullTruck(final Audience audience) {
    if (this.truck == null) {
      final Component message = Message.ARENA_TRUCK_ERROR.build();
      audience.sendMessage(message);
      return true;
    }
    return false;
  }

  private boolean handleNullName(final Audience audience) {
    if (this.name == null) {
      final Component message = Message.ARENA_NAME_ERROR.build();
      audience.sendMessage(message);
      return true;
    }
    return false;
  }

  @Permission("murderrun.command.arena.set.name")
  @CommandDescription("murderrun.command.arena.set.name.info")
  @Command(value = "murder arena set name <name>", requiredSender = Player.class)
  public void setName(final Player sender, @Quoted final String name) {
    this.name = name;
    final Audience audience = this.audiences.player(sender);
    final Component message = Message.ARENA_NAME.build(name);
    audience.sendMessage(message);
  }

  @Permission("murderrun.command.arena.set.spawn")
  @CommandDescription("murderrun.command.arena.set.spawn.info")
  @Command(value = "murder arena set spawn", requiredSender = Player.class)
  public void setSpawn(final Player sender) {
    final Location location = sender.getLocation();
    this.spawn = location;
    final Audience audience = this.audiences.player(sender);
    final Component message = ComponentUtils.createLocationComponent(Message.ARENA_SPAWN, location);
    audience.sendMessage(message);
  }

  @Permission("murderrun.command.arena.set.truck")
  @CommandDescription("murderrun.command.arena.set.truck.info")
  @Command(value = "murder arena set truck", requiredSender = Player.class)
  public void setTruck(final Player sender) {
    final Location location = sender.getLocation();
    this.truck = location;
    final Audience audience = this.audiences.player(sender);
    final Component message = ComponentUtils.createLocationComponent(Message.ARENA_TRUCK, location);
    audience.sendMessage(message);
  }

  @Permission("murderrun.command.arena.set.corner.first")
  @CommandDescription("murderrun.command.arena.set.corner.first.info")
  @Command(value = "murder arena set first-corner", requiredSender = Player.class)
  public void setFirstCorner(final Player sender) {
    final Location location = sender.getLocation();
    this.first = location;
    final Audience audience = this.audiences.player(sender);
    final Component message = ComponentUtils.createLocationComponent(Message.ARENA_FIRST_CORNER, location);
    audience.sendMessage(message);
  }

  @Permission("murderrun.command.arena.set.corner.second")
  @CommandDescription("murderrun.command.arena.set.corner.second.info")
  @Command(value = "murder arena set second-corner", requiredSender = Player.class)
  public void setSecondCorner(final Player sender) {
    final Location location = sender.getLocation();
    this.second = location;
    final Audience audience = this.audiences.player(sender);
    final Component message = ComponentUtils.createLocationComponent(Message.ARENA_SECOND_CORNER, location);
    audience.sendMessage(message);
  }
}
