package io.github.pulsebeat02.murderrun.commmand.game;

import io.github.pulsebeat02.murderrun.MurderRun;
import io.github.pulsebeat02.murderrun.game.Game;
import io.github.pulsebeat02.murderrun.game.GameStatus;
import io.github.pulsebeat02.murderrun.game.arena.Arena;
import io.github.pulsebeat02.murderrun.game.arena.ArenaManager;
import io.github.pulsebeat02.murderrun.game.lobby.*;
import io.github.pulsebeat02.murderrun.locale.Message;
import net.kyori.adventure.audience.Audience;
import net.kyori.adventure.text.Component;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.checkerframework.checker.nullness.qual.EnsuresNonNullIf;
import org.checkerframework.checker.nullness.qual.Nullable;

public final class GameInputSanitizer {

  private final GameCommand command;

  public GameInputSanitizer(final GameCommand command) {
    this.command = command;
  }

  public boolean checkIfNoQuickJoinableGame(final Player sender, final Audience audience, final GameManager manager) {
    final boolean success = manager.quickJoinGame(sender);
    if (!success) {
      final Component message = Message.GAME_NONE.build();
      audience.sendMessage(message);
      return true;
    }
    return false;
  }

  public boolean checkIfGameFull(
    final Player sender,
    final Audience audience,
    final GameManager manager,
    final PreGameManager game
  ) {
    final String id = game.getId();
    final boolean success = manager.joinGame(sender, id);
    if (!success) {
      final Component message = Message.GAME_FULL.build();
      audience.sendMessage(message);
      return true;
    }
    return false;
  }

  @EnsuresNonNullIf(expression = "#2", result = false)
  public boolean checkIfInNoGame(final Audience audience, final @Nullable PreGameManager data) {
    if (data == null) {
      final Component message = Message.GAME_INVALID_ERROR.build();
      audience.sendMessage(message);
      return true;
    }
    return false;
  }

  public boolean checkIfNotOwner(final CommandSender sender, final Audience audience, final PreGameManager data) {
    final PreGamePlayerManager manager = data.getPlayerManager();
    if (!manager.isLeader(sender)) {
      final Component message = Message.GAME_NOT_OWNER_ERROR.build();
      audience.sendMessage(message);
      return true;
    }
    return false;
  }

  public boolean checkIfGameAlreadyStarted(final Audience audience, final PreGameManager data) {
    final Game game = data.getGame();
    final GameStatus status = game.getStatus();
    if (status == GameStatus.SURVIVORS_RELEASED) {
      final Component message = Message.GAME_STARTED_ERROR.build();
      audience.sendMessage(message);
      return true;
    }
    return false;
  }

  public boolean checkIfNotEnoughPlayers(final Audience audience, final PreGameManager data) {
    final PreGamePlayerManager manager = data.getPlayerManager();
    if (!manager.isEnoughPlayers()) {
      final Component message = Message.GAME_LOW_PLAYER_COUNT_ERROR.build();
      audience.sendMessage(message);
      return true;
    }
    return false;
  }

  public boolean checkIfAlreadyInGame(final Audience audience, final @Nullable PreGameManager temp) {
    if (temp != null) {
      final Component message = Message.GAME_CREATE_ERROR.build();
      audience.sendMessage(message);
      return true;
    }
    return false;
  }

  public boolean checkIfArenaValid(final Audience audience, final String arenaName) {
    final MurderRun plugin = this.command.getPlugin();
    final ArenaManager arenaManager = plugin.getArenaManager();
    final Arena arena = arenaManager.getArena(arenaName);
    if (arena == null) {
      final Component message = Message.GAME_ARENA_ERROR.build();
      audience.sendMessage(message);
      return true;
    }
    return false;
  }

  public boolean checkIfLobbyValid(final Audience audience, final String lobbyName) {
    final MurderRun plugin = this.command.getPlugin();
    final LobbyManager lobbyManager = plugin.getLobbyManager();
    final Lobby lobby = lobbyManager.getLobby(lobbyName);
    if (lobby == null) {
      final Component message = Message.GAME_LOBBY_ERROR.build();
      audience.sendMessage(message);
      return true;
    }
    return false;
  }

  public boolean checkIfNotSamePlayer(final Audience audience, final Player sender, final Player invite) {
    if (sender == invite) {
      final Component message = Message.GAME_INVITE_ERROR.build();
      audience.sendMessage(message);
      return true;
    }
    return false;
  }

  public boolean checkIfOwnerOfCurrentGame(
    final CommandSender sender,
    final Audience audience,
    final PreGameManager temp
  ) {
    final PreGamePlayerManager manager = temp.getPlayerManager();
    if (manager.isLeader(sender)) {
      final Component message = Message.GAME_LEAVE_ERROR.build();
      audience.sendMessage(message);
      return true;
    }
    return false;
  }

  public boolean checkIfInvitedAlreadyInGame(final Audience audience, final Player invite, final PreGameManager data) {
    final GameManager manager = this.command.getGameManager();
    final PreGameManager otherPlayerData = manager.getGame(invite);
    if (otherPlayerData == null) {
      return false;
    }

    if (data == otherPlayerData) {
      final Component message = Message.GAME_INVITE_ALREADY_ERROR.build();
      audience.sendMessage(message);
      return true;
    }

    return false;
  }

  public boolean checkIfNotInvited(final Audience audience, final Player sender, final String id) {
    final GameManager manager = this.command.getGameManager();
    final PreGameManager data = manager.getGame(id);
    if (data == null) {
      final Component message = Message.GAME_INVALID_ERROR.build();
      audience.sendMessage(message);
      return true;
    }

    final PreGamePlayerManager playerManager = data.getPlayerManager();
    final CommandSender owner = playerManager.getLeader();
    final InviteManager invites = this.command.getInviteManager();
    if (!(invites.hasInvite(owner, sender) || playerManager.isQuickJoinable())) {
      final Component message = Message.GAME_INVALID_INVITE_ERROR.build();
      audience.sendMessage(message);
      return true;
    }

    return false;
  }

  public boolean checkIfInvalidPlayerCounts(final Audience audience, final int min, final int max) {
    if (min > max || min < 2) {
      final Component message = Message.GAME_PLAYER_ERROR.build();
      audience.sendMessage(message);
      return true;
    }
    return false;
  }
}
