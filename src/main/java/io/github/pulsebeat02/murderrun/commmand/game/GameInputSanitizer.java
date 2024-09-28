package io.github.pulsebeat02.murderrun.commmand.game;

import io.github.pulsebeat02.murderrun.MurderRun;
import io.github.pulsebeat02.murderrun.game.Game;
import io.github.pulsebeat02.murderrun.game.GameStatus;
import io.github.pulsebeat02.murderrun.game.arena.Arena;
import io.github.pulsebeat02.murderrun.game.arena.ArenaManager;
import io.github.pulsebeat02.murderrun.game.lobby.Lobby;
import io.github.pulsebeat02.murderrun.game.lobby.LobbyManager;
import io.github.pulsebeat02.murderrun.game.lobby.PreGameManager;
import io.github.pulsebeat02.murderrun.game.lobby.PreGamePlayerManager;
import io.github.pulsebeat02.murderrun.locale.Message;
import net.kyori.adventure.audience.Audience;
import net.kyori.adventure.text.Component;
import org.bukkit.command.CommandSender;
import org.checkerframework.checker.nullness.qual.EnsuresNonNullIf;
import org.checkerframework.checker.nullness.qual.Nullable;

public final class GameInputSanitizer {

  private final GameCommand command;

  public GameInputSanitizer(final GameCommand command) {
    this.command = command;
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

  public boolean checkIfNotOwner(
      final CommandSender sender, final Audience audience, final PreGameManager data) {
    final PreGamePlayerManager manager = data.getManager();
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
    final PreGamePlayerManager manager = data.getManager();
    if (!manager.isEnoughPlayers()) {
      final Component message = Message.GAME_LOW_PLAYER_COUNT_ERROR.build();
      audience.sendMessage(message);
      return true;
    }
    return false;
  }

  public boolean checkIfAlreadyInGame(final Audience audience, final PreGameManager temp) {
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
}
