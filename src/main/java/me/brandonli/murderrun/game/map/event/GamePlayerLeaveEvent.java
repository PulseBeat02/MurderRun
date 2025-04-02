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
package me.brandonli.murderrun.game.map.event;

import static java.util.Objects.requireNonNull;

import java.util.UUID;
import me.brandonli.murderrun.MurderRun;
import me.brandonli.murderrun.game.Game;
import me.brandonli.murderrun.game.GameProperties;
import me.brandonli.murderrun.game.lobby.GameManager;
import me.brandonli.murderrun.game.lobby.PreGameManager;
import me.brandonli.murderrun.game.lobby.PreGamePlayerManager;
import me.brandonli.murderrun.game.player.GamePlayer;
import me.brandonli.murderrun.game.player.GamePlayerManager;
import me.brandonli.murderrun.game.player.phase.PlayerResetTool;
import org.bukkit.Bukkit;
import org.bukkit.Server;
import org.bukkit.command.ConsoleCommandSender;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.player.PlayerQuitEvent;

public final class GamePlayerLeaveEvent extends GameEvent {

  public GamePlayerLeaveEvent(final Game game) {
    super(game);
  }

  @EventHandler(priority = EventPriority.LOWEST)
  private void onPlayerDisconnect(final PlayerQuitEvent event) {
    final Player player = event.getPlayer();
    if (!this.isGamePlayer(player)) {
      return;
    }

    final Game game = this.getGame();
    final GamePlayerManager manager = game.getPlayerManager();
    final GamePlayer gamePlayer = manager.getGamePlayer(player);
    gamePlayer.setLoggingOut(true);

    final String commands = GameProperties.PLAYER_LEAVE_COMMANDS_AFTER;
    if (!commands.equals("none")) {
      final String[] split = commands.split(",");
      final Server server = Bukkit.getServer();
      final ConsoleCommandSender console = server.getConsoleSender();
      for (final String command : split) {
        server.dispatchCommand(console, command);
      }
    }

    if (gamePlayer.isAlive()) {
      player.setHealth(0f);
    }

    final PlayerResetTool resetTool = new PlayerResetTool(manager);
    resetTool.handlePlayer(gamePlayer);

    final MurderRun plugin = game.getPlugin();
    final GameManager gameManager = plugin.getGameManager();
    final PreGameManager preGameManager = requireNonNull(gameManager.getGameAsParticipant(player));
    final PreGamePlayerManager preGamePlayerManager = preGameManager.getPlayerManager();
    preGamePlayerManager.removeParticipantFromGameInternal(player);

    final UUID uuid = gamePlayer.getUUID();
    manager.removePlayer(uuid);
  }
}
