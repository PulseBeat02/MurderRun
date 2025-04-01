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
package io.github.pulsebeat02.murderrun.commmand.game;

import io.github.pulsebeat02.murderrun.MurderRun;
import io.github.pulsebeat02.murderrun.game.Game;
import io.github.pulsebeat02.murderrun.game.GameStatus;
import io.github.pulsebeat02.murderrun.game.arena.Arena;
import io.github.pulsebeat02.murderrun.game.arena.ArenaManager;
import io.github.pulsebeat02.murderrun.game.capability.Capabilities;
import io.github.pulsebeat02.murderrun.game.extension.parties.PartiesManager;
import io.github.pulsebeat02.murderrun.game.lobby.*;
import io.github.pulsebeat02.murderrun.locale.Message;
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

  public boolean checkIfNoQuickJoinableGame(final Player sender, final Audience audience, final GameManager manager) {
    final boolean success = manager.quickJoinGame(sender);
    if (!success) {
      audience.sendMessage(Message.GAME_NONE.build());
      return true;
    }
    return false;
  }

  public boolean checkIfGameFull(final Player sender, final Audience audience, final GameManager manager, final PreGameManager game) {
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

  public boolean checkIfNotOwner(final CommandSender sender, final Audience audience, final PreGameManager data) {
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

  public boolean checkIfAlreadyInGame(final Audience audience, final @Nullable PreGameManager temp) {
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

  public boolean checkIfNotSamePlayer(final Audience audience, final Player sender, final Player invite) {
    if (sender == invite) {
      audience.sendMessage(Message.GAME_INVITE_ERROR.build());
      return true;
    }
    return false;
  }

  public boolean checkIfOwnerOfCurrentGame(final CommandSender sender, final Audience audience, final PreGameManager temp) {
    final PreGamePlayerManager manager = temp.getPlayerManager();
    if (manager.isLeader(sender)) {
      audience.sendMessage(Message.GAME_LEAVE_ERROR.build());
      return true;
    }
    return false;
  }

  public boolean checkIfInvitedAlreadyInGame(final Audience audience, final Player invite, final PreGameManager data) {
    final GameManager manager = this.command.getGameManager();
    final PreGameManager otherPlayerData = manager.getGame(invite);
    if (otherPlayerData != null && data == otherPlayerData) {
      audience.sendMessage(Message.GAME_INVITE_ALREADY_ERROR.build());
      return true;
    }
    return false;
  }

  public boolean checkIfNotInvited(final Audience audience, final Player sender, final String id) {
    final GameManager manager = this.command.getGameManager();
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
    final GameManager manager = this.command.getGameManager();
    if (manager.getGame(id) != null) {
      audience.sendMessage(Message.GAME_ID_EXISTS_ERROR.build());
      return true;
    }
    return false;
  }
}
