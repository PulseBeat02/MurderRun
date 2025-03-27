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
import io.github.pulsebeat02.murderrun.game.lobby.*;
import io.github.pulsebeat02.murderrun.locale.Message;
import net.kyori.adventure.audience.Audience;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.checkerframework.checker.nullness.qual.EnsuresNonNullIf;
import org.checkerframework.checker.nullness.qual.Nullable;

/**
 * 游戏指令输入验证器，用于校验游戏相关指令的合法性，确保游戏逻辑正确执行。
 * 提供了一系列静态检查方法，每个方法负责特定的验证逻辑，并在验证失败时发送错误消息。
 */
public final class GameInputSanitizer {

  // 关联的游戏指令处理器
  private final GameCommand command;

  /**
   * 构造方法，初始化游戏指令输入验证器。
   * @param command 关联的GameCommand实例
   */
  public GameInputSanitizer(final GameCommand command) {
    this.command = command;
  }

  /**
   * 检查是否存在可快速加入的游戏。若不存在，发送错误消息。
   * @param sender   执行指令的玩家
   * @param audience 消息接收者（一般为玩家）
   * @param manager  游戏管理器
   * @return 是否存在可加入游戏：true-无可用游戏，false-存在可加入游戏
   */
  public boolean checkIfNoQuickJoinableGame(final Player sender, final Audience audience, final GameManager manager) {
    final boolean success = manager.quickJoinGame(sender);
    if (!success) {
      audience.sendMessage(Message.GAME_NONE.build());
      return true;
    }
    return false;
  }

  /**
   * 检查目标游戏是否已满员。若已满，发送错误消息。
   * @param sender   执行指令的玩家
   * @param audience 消息接收者
   * @param manager  游戏管理器
   * @param game     预游戏管理器实例
   * @return 是否已满：true-已满，false-未满
   */
  public boolean checkIfGameFull(final Player sender, final Audience audience, final GameManager manager, final PreGameManager game) {
    final String id = game.getId();
    final boolean success = manager.joinGame(sender, id);
    if (!success) {
      audience.sendMessage(Message.GAME_FULL.build());
      return true;
    }
    return false;
  }

  /**
   * 检查玩家是否未加入任何游戏。若未加入，发送错误消息。
   * @param audience 消息接收者
   * @param data     预游戏管理器实例（可为空）
   * @return 是否未加入游戏：true-未加入，false-已加入
   */
  @EnsuresNonNullIf(expression = "#2", result = false)
  public boolean checkIfInNoGame(final Audience audience, final @Nullable PreGameManager data) {
    if (data == null) {
      audience.sendMessage(Message.GAME_INVALID_ERROR.build());
      return true;
    }
    return false;
  }

  /**
   * 检查玩家是否为游戏房主。若不是，发送错误消息。
   * @param sender   指令发送者
   * @param audience 消息接收者
   * @param data     预游戏管理器实例
   * @return 是否为房主：true-不是房主，false-是房主
   */
  public boolean checkIfNotOwner(final CommandSender sender, final Audience audience, final PreGameManager data) {
    final PreGamePlayerManager manager = data.getPlayerManager();
    if (!manager.isLeader(sender)) {
      audience.sendMessage(Message.GAME_NOT_OWNER_ERROR.build());
      return true;
    }
    return false;
  }

  /**
   * 检查游戏是否已经开始。若已开始，发送错误消息。
   * @param audience 消息接收者
   * @param data     预游戏管理器实例
   * @return 是否已开始：true-已开始，false-未开始
   */
  public boolean checkIfGameAlreadyStarted(final Audience audience, final PreGameManager data) {
    final Game game = data.getGame();
    final GameStatus status = game.getStatus();
    if (status.getStatus() == GameStatus.Status.SURVIVORS_RELEASED) {
      audience.sendMessage(Message.GAME_STARTED_ERROR.build());
      return true;
    }
    return false;
  }

  /**
   * 检查游戏人数是否不足。若不足，发送错误消息。
   * @param audience 消息接收者
   * @param data     预游戏管理器实例
   * @return 人数是否不足：true-不足，false-足够
   */
  public boolean checkIfNotEnoughPlayers(final Audience audience, final PreGameManager data) {
    final PreGamePlayerManager manager = data.getPlayerManager();
    if (!manager.isEnoughPlayers()) {
      audience.sendMessage(Message.GAME_LOW_PLAYER_COUNT_ERROR.build());
      return true;
    }
    return false;
  }

  /**
   * 检查玩家是否已加入其他游戏。若已加入，发送错误消息。
   * @param audience 消息接收者
   * @param temp     预游戏管理器实例（可为空）
   * @return 是否已加入游戏：true-已加入，false-未加入
   */
  public boolean checkIfAlreadyInGame(final Audience audience, final @Nullable PreGameManager temp) {
    if (temp != null) {
      audience.sendMessage(Message.GAME_CREATE_ERROR.build());
      return true;
    }
    return false;
  }

  /**
   * 检查指定竞技场是否存在。若不存在，发送错误消息。
   * @param audience   消息接收者
   * @param arenaName 竞技场名称
   * @return 是否存在：true-不存在，false-存在
   */
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

  /**
   * 检查指定大厅是否存在。若不存在，发送错误消息。
   * @param audience  消息接收者
   * @param lobbyName 大厅名称
   * @return 是否存在：true-不存在，false-存在
   */
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

  /**
   * 检查邀请者与被邀请者是否为同一玩家。若是，发送错误消息。
   * @param audience 消息接收者
   * @param sender   邀请者
   * @param invite   被邀请者
   * @return 是否相同：true-相同，false-不同
   */
  public boolean checkIfNotSamePlayer(final Audience audience, final Player sender, final Player invite) {
    if (sender == invite) {
      audience.sendMessage(Message.GAME_INVITE_ERROR.build());
      return true;
    }
    return false;
  }

  /**
   * 检查玩家是否为当前游戏的房主（房主不可离开）。若是，发送错误消息。
   * @param sender   指令发送者
   * @param audience 消息接收者
   * @param temp     预游戏管理器实例
   * @return 是否为房主：true-是房主，false-不是房主
   */
  public boolean checkIfOwnerOfCurrentGame(final CommandSender sender, final Audience audience, final PreGameManager temp) {
    final PreGamePlayerManager manager = temp.getPlayerManager();
    if (manager.isLeader(sender)) {
      audience.sendMessage(Message.GAME_LEAVE_ERROR.build());
      return true;
    }
    return false;
  }

  /**
   * 检查被邀请者是否已加入当前游戏。若是，发送错误消息。
   * @param audience 消息接收者
   * @param invite   被邀请的玩家
   * @param data     当前预游戏管理器实例
   * @return 是否已加入：true-已加入，false-未加入
   */
  public boolean checkIfInvitedAlreadyInGame(final Audience audience, final Player invite, final PreGameManager data) {
    final GameManager manager = this.command.getGameManager();
    final PreGameManager otherPlayerData = manager.getGame(invite);
    if (otherPlayerData != null && data == otherPlayerData) {
      audience.sendMessage(Message.GAME_INVITE_ALREADY_ERROR.build());
      return true;
    }
    return false;
  }

  /**
   * 检查玩家是否有权限加入指定游戏（是否被邀请或允许快速加入）。若无权限，发送错误消息。
   * @param audience 消息接收者
   * @param sender   尝试加入的玩家
   * @param id       游戏ID
   * @return 是否有权限：true-无权限，false-有权限
   */
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

  /**
   * 检查设置的玩家数量范围是否有效（最小值不能大于最大值且至少为2）。若无效，发送错误消息。
   * @param audience 消息接收者
   * @param min      最小玩家数
   * @param max      最大玩家数
   * @return 是否无效：true-无效，false-有效
   */
  public boolean checkIfInvalidPlayerCounts(final Audience audience, final int min, final int max) {
    if (min > max || min < 2) {
      audience.sendMessage(Message.GAME_PLAYER_ERROR.build());
      return true;
    }
    return false;
  }

  /**
   * 检查指定游戏 id 是否已存在。若存在，发送错误消息。
   * @param audience 消息接收者
   * @param id 游戏 id
   * @return 是否已存在：true-已存在，false-不存在
   */
  public boolean checkIfGameIdExists(final Audience audience, final String id) {
    final GameManager manager = this.command.getGameManager();
    if (manager.getGame(id) != null) {
      audience.sendMessage(Message.GAME_ID_EXISTS_ERROR.build());
      return true;
    }
    return false;
  }
}
