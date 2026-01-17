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
package me.brandonli.murderrun.commmand.game;

import java.util.Optional;
import me.brandonli.murderrun.MurderRun;
import me.brandonli.murderrun.game.Game;
import me.brandonli.murderrun.game.GameMode;
import me.brandonli.murderrun.game.GameStatus;
import me.brandonli.murderrun.game.arena.Arena;
import me.brandonli.murderrun.game.arena.ArenaManager;
import me.brandonli.murderrun.game.capability.Capabilities;
import me.brandonli.murderrun.game.extension.parties.PartiesManager;
import me.brandonli.murderrun.game.lobby.*;
import me.brandonli.murderrun.locale.Message;
import net.kyori.adventure.audience.Audience;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.checkerframework.checker.nullness.qual.EnsuresNonNullIf;
import org.checkerframework.checker.nullness.qual.Nullable;

public final class GameInputSanitizer {

  private final GameCommand command;

  public GameInputSanitizer(final GameCommand command) {
    this.command = command;
  }

  public boolean checkIfPartyNotLeader(final Player player, final Audience audience) {
    final MurderRun plugin = this.command.getPlugin();
    final PartiesManager manager = plugin.getPartiesManager();
    if (!manager.isLeader(player)) {
      audience.sendMessage(Message.GAME_PARTY_LEADER_ERROR.build());
      return true;
    }
    return false;
  }

  public boolean checkIfPartyInvalid(final Player player, final Audience audience) {
    final MurderRun plugin = this.command.getPlugin();
    final PartiesManager manager = plugin.getPartiesManager();
    if (!manager.isInParty(player)) {
      audience.sendMessage(Message.GAME_PARTY_EMPTY.build());
      return true;
    }
    return false;
  }

  public boolean checkIfPartyCapabilityDisabled(final Player player, final Audience audience) {
    if (Capabilities.PARTIES.isDisabled()) {
      audience.sendMessage(Message.GAME_PARTY_ERROR.build());
      return true;
    }
    return false;
  }

  public boolean checkIfNoQuickJoinableGame(final Player sender, final GameManager manager) {
    return manager.quickJoinGame(sender);
  }

  public boolean checkIfGameFull(
      final Player sender,
      final Audience audience,
      final GameManager manager,
      final PreGameManager game) {
    final String id = game.getId();
    final boolean success = manager.joinGame(sender, id);
    if (!success) {
      audience.sendMessage(Message.GAME_FULL.build());
      return true;
    }
    return false;
  }

  @EnsuresNonNullIf(expression = "#2", result = false)
  public boolean checkIfInNoGame(final Audience audience, final @Nullable PreGameManager data) {
    if (data == null) {
      audience.sendMessage(Message.GAME_INVALID_ERROR.build());
      return true;
    }
    return false;
  }

  public boolean checkIfGameHasStarted(final Audience audience, final PreGameManager data) {
    final Game game = data.getGame();
    final GameStatus status = game.getStatus();
    final GameStatus.Status gameStatus = status.getStatus();
    if (gameStatus != GameStatus.Status.NOT_STARTED) {
      audience.sendMessage(Message.GAME_ERROR_STARTED.build());
      return true;
    }
    return false;
  }

  public boolean checkIfNotOwner(
      final CommandSender sender, final Audience audience, final PreGameManager data) {
    final PreGamePlayerManager manager = data.getPlayerManager();
    if (!manager.isLeader(sender)) {
      audience.sendMessage(Message.GAME_NOT_OWNER_ERROR.build());
      return true;
    }
    return false;
  }

  public boolean checkIfGameAlreadyStarted(final Audience audience, final PreGameManager data) {
    final Game game = data.getGame();
    final GameStatus status = game.getStatus();
    if (status.getStatus() == GameStatus.Status.SURVIVORS_RELEASED) {
      audience.sendMessage(Message.GAME_STARTED_ERROR.build());
      return true;
    }
    return false;
  }

  public boolean checkIfNotEnoughPlayers(final Audience audience, final PreGameManager data) {
    final PreGamePlayerManager manager = data.getPlayerManager();
    if (!manager.isEnoughPlayers()) {
      audience.sendMessage(Message.GAME_LOW_PLAYER_COUNT_ERROR.build());
      return true;
    }
    return false;
  }

  public boolean checkIfGameModeExists(final Audience audience, final String modeName) {
    final Optional<GameMode> mode = GameMode.fromString(modeName);
    if (mode.isEmpty()) {
      audience.sendMessage(Message.GAME_MODE_ERROR.build());
      return true;
    }
    return false;
  }

  public boolean checkIfAlreadyInGame(
      final Audience audience, final @Nullable PreGameManager temp) {
    if (temp != null) {
      audience.sendMessage(Message.GAME_CREATE_ERROR.build());
      return true;
    }
    return false;
  }

  public boolean checkIfArenaValid(final Audience audience, final String arenaName) {
    final MurderRun plugin = this.command.getPlugin();
    final ArenaManager arenaManager = plugin.getArenaManager();
    final Arena arena = arenaManager.getArena(arenaName);
    if (arena == null) {
      audience.sendMessage(Message.GAME_ARENA_ERROR.build());
      return true;
    }
    return false;
  }

  public boolean checkIfLobbyValid(final Audience audience, final String lobbyName) {
    final MurderRun plugin = this.command.getPlugin();
    final LobbyManager lobbyManager = plugin.getLobbyManager();
    final Lobby lobby = lobbyManager.getLobby(lobbyName);
    if (lobby == null) {
      audience.sendMessage(Message.GAME_LOBBY_ERROR.build());
      return true;
    }
    return false;
  }

  public boolean checkIfNotSamePlayer(
      final Audience audience, final Player sender, final Player invite) {
    if (sender == invite) {
      audience.sendMessage(Message.GAME_INVITE_ERROR.build());
      return true;
    }
    return false;
  }

  public boolean checkIfOwnerOfCurrentGame(
      final CommandSender sender, final Audience audience, final PreGameManager temp) {
    final PreGamePlayerManager manager = temp.getPlayerManager();
    if (manager.isLeader(sender)) {
      audience.sendMessage(Message.GAME_LEAVE_ERROR.build());
      return true;
    }
    return false;
  }

  public boolean checkIfInvitedAlreadyInGame(
      final Audience audience, final Player invite, final PreGameManager data) {
    final MurderRun plugin = this.command.getPlugin();
    final GameManager manager = plugin.getGameManager();
    final PreGameManager otherPlayerData = manager.getGame(invite);
    if (otherPlayerData != null && data == otherPlayerData) {
      audience.sendMessage(Message.GAME_INVITE_ALREADY_ERROR.build());
      return true;
    }
    return false;
  }

  public boolean checkIfNotInvited(final Audience audience, final Player sender, final String id) {
    final MurderRun plugin = this.command.getPlugin();
    final GameManager manager = plugin.getGameManager();
    final PreGameManager data = manager.getGame(id);
    if (data == null) {
      audience.sendMessage(Message.GAME_INVALID_ERROR.build());
      return true;
    }

    final PreGamePlayerManager playerManager = data.getPlayerManager();
    final CommandSender owner = playerManager.getLeader();
    final InviteManager invites = this.command.getInviteManager();
    final boolean canJoin = invites.hasInvite(owner, sender) || playerManager.isQuickJoinable();
    if (!canJoin) {
      audience.sendMessage(Message.GAME_INVALID_INVITE_ERROR.build());
      return true;
    }

    return false;
  }

  public boolean checkIfInvalidPlayerCounts(final Audience audience, final int min, final int max) {
    if (min > max || min < 2) {
      audience.sendMessage(Message.GAME_PLAYER_ERROR.build());
      return true;
    }
    return false;
  }

  public boolean checkIfGameIdExists(final Audience audience, final String id) {
    final MurderRun plugin = this.command.getPlugin();
    final GameManager manager = plugin.getGameManager();
    if (manager.getGame(id) != null) {
      audience.sendMessage(Message.GAME_ID_EXISTS_ERROR.build());
      return true;
    }
    return false;
  }
}
