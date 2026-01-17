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

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import me.brandonli.murderrun.MurderRun;
import me.brandonli.murderrun.game.lobby.Lobby;
import me.brandonli.murderrun.game.lobby.LobbyManager;
import me.brandonli.murderrun.locale.AudienceProvider;
import me.brandonli.murderrun.locale.Message;
import me.brandonli.murderrun.utils.ComponentUtils;
import me.brandonli.murderrun.utils.map.MapUtils;
import net.kyori.adventure.audience.Audience;
import net.kyori.adventure.platform.bukkit.BukkitAudiences;
import net.kyori.adventure.text.Component;
import org.bukkit.Location;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.incendo.cloud.annotation.specifier.Quoted;
import org.incendo.cloud.annotations.AnnotationParser;
import org.incendo.cloud.annotations.Command;
import org.incendo.cloud.annotations.CommandDescription;
import org.incendo.cloud.annotations.Permission;

public final class LobbyCommand implements AnnotationCommandFeature {

  private MurderRun plugin;
  private BukkitAudiences audiences;
  private Location spawn;
  private Location first;
  private Location second;
  private String name;

  @Override
  public void registerFeature(
      final MurderRun plugin, final AnnotationParser<CommandSender> parser) {
    final AudienceProvider handler = plugin.getAudience();
    this.audiences = handler.retrieve();
    this.plugin = plugin;
  }

  @Permission("murderrun.command.lobby.set.corner.first")
  @CommandDescription("murderrun.command.lobby.set.corner.first.info")
  @Command(value = "murder lobby set first-corner", requiredSender = Player.class)
  public void setFirstCorner(final Player sender) {
    final Location location = sender.getLocation();
    this.first = location;
    final Audience audience = this.audiences.player(sender);
    final Component message =
        ComponentUtils.createLocationComponent(Message.LOBBY_FIRST_CORNER, location);
    audience.sendMessage(message);
  }

  @Permission("murderrun.command.lobby.set.corner.second")
  @CommandDescription("murderrun.command.lobby.set.corner.second.info")
  @Command(value = "murder lobby set second-corner", requiredSender = Player.class)
  public void setSecondCorner(final Player sender) {
    final Location location = sender.getLocation();
    this.second = location;
    final Audience audience = this.audiences.player(sender);
    final Component message =
        ComponentUtils.createLocationComponent(Message.LOBBY_SECOND_CORNER, location);
    audience.sendMessage(message);
  }

  @Permission("murderrun.command.lobby.list")
  @CommandDescription("murderrun.command.lobby.list.info")
  @Command(value = "murder lobby list", requiredSender = Player.class)
  public void listLobbies(final Player sender) {
    final LobbyManager manager = this.plugin.getLobbyManager();
    final Map<String, Lobby> arenas = manager.getLobbies();
    final List<String> keys = new ArrayList<>(arenas.keySet());
    final Audience audience = this.audiences.player(sender);
    final Component message = Message.LOBBY_LIST.build(keys);
    audience.sendMessage(message);
  }

  @Permission("murderrun.command.lobby.remove")
  @CommandDescription("murderrun.command.lobby.remove.info")
  @Command(value = "murder lobby remove <name>", requiredSender = Player.class)
  public void removeLobby(final Player sender, final String name) {
    final Audience audience = this.audiences.player(sender);
    if (this.checkInvalidLobby(audience, name)) {
      return;
    }

    final LobbyManager manager = this.plugin.getLobbyManager();
    manager.removeLobby(name);

    final Component message = Message.LOBBY_REMOVE.build(name);
    audience.sendMessage(message);
  }

  private boolean checkInvalidLobby(final Audience audience, final String name) {
    final LobbyManager manager = this.plugin.getLobbyManager();
    final Map<String, Lobby> arenas = manager.getLobbies();
    final Lobby lobby = arenas.get(name);
    if (lobby == null) {
      final Component message = Message.LOBBY_REMOVE_ERROR.build();
      audience.sendMessage(message);
      return true;
    }
    return false;
  }

  @Permission("murderrun.command.lobby.create")
  @CommandDescription("murderrun.command.lobby.create.info")
  @Command(value = "murder lobby create", requiredSender = Player.class)
  public void createLobby(final Player sender) {
    final Audience audience = this.audiences.player(sender);
    if (this.handleNullSpawn(audience) || this.handleNullName(audience)) {
      return;
    }

    final Component msg = Message.LOBBY_CREATE_LOAD.build();
    audience.sendMessage(msg);

    final Location[] corners = {this.first, this.second};
    final LobbyManager manager = this.plugin.getLobbyManager();
    final Location actual = MapUtils.getSafeSpawn(this.spawn);
    manager.addLobby(this.name, corners, actual);

    this.plugin.updatePluginData();

    final Component message = Message.LOBBY_BUILT.build();
    audience.sendMessage(message);
  }

  private boolean handleNullSpawn(final Audience audience) {
    if (this.spawn == null) {
      final Component message = Message.LOBBY_SPAWN_ERROR.build();
      audience.sendMessage(message);
      return true;
    }
    return false;
  }

  private boolean handleNullName(final Audience audience) {
    if (this.name == null) {
      final Component message = Message.LOBBY_NAME_ERROR.build();
      audience.sendMessage(message);
      return true;
    }
    return false;
  }

  @Permission("murderrun.command.lobby.set.name")
  @CommandDescription("murderrun.command.lobby.set.name.info")
  @Command(value = "murder lobby set name <name>", requiredSender = Player.class)
  public void setName(final Player sender, @Quoted final String name) {
    this.name = name;
    final Audience audience = this.audiences.player(sender);
    final Component message = Message.LOBBY_NAME.build(name);
    audience.sendMessage(message);
  }

  @Permission("murderrun.command.lobby.set.spawn")
  @CommandDescription("murderrun.command.lobby.set.spawn.info")
  @Command(value = "murder lobby set spawn", requiredSender = Player.class)
  public void setSpawn(final Player sender) {
    final Location location = sender.getLocation();
    this.spawn = location;
    final Audience audience = this.audiences.player(sender);
    final Component message = ComponentUtils.createLocationComponent(Message.LOBBY_SPAWN, location);
    audience.sendMessage(message);
  }
}
