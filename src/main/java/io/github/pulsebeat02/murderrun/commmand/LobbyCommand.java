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
package io.github.pulsebeat02.murderrun.commmand;

import io.github.pulsebeat02.murderrun.MurderRun;
import io.github.pulsebeat02.murderrun.game.lobby.Lobby;
import io.github.pulsebeat02.murderrun.game.lobby.LobbyManager;
import io.github.pulsebeat02.murderrun.locale.AudienceProvider;
import io.github.pulsebeat02.murderrun.locale.Message;
import io.github.pulsebeat02.murderrun.utils.ComponentUtils;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
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
  public void registerFeature(final MurderRun plugin, final AnnotationParser<CommandSender> parser) {
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
    final Component message = ComponentUtils.createLocationComponent(Message.LOBBY_FIRST_CORNER, location);
    audience.sendMessage(message);
  }

  @Permission("murderrun.command.lobby.set.corner.second")
  @CommandDescription("murderrun.command.lobby.set.corner.second.info")
  @Command(value = "murder lobby set second-corner", requiredSender = Player.class)
  public void setSecondCorner(final Player sender) {
    final Location location = sender.getLocation();
    this.second = location;
    final Audience audience = this.audiences.player(sender);
    final Component message = ComponentUtils.createLocationComponent(Message.LOBBY_SECOND_CORNER, location);
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

    final Location[] corners = { this.first, this.second };
    final LobbyManager manager = this.plugin.getLobbyManager();
    manager.addLobby(this.name, corners, this.spawn);

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
