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
package io.github.pulsebeat02.murderrun.game.lobby;

import static java.util.Objects.requireNonNull;
import static net.kyori.adventure.text.Component.empty;

import io.github.pulsebeat02.murderrun.game.GameProperties;
import io.github.pulsebeat02.murderrun.game.GameSettings;
import io.github.pulsebeat02.murderrun.game.arena.Arena;
import io.github.pulsebeat02.murderrun.locale.Message;
import java.util.Collection;
import net.kyori.adventure.text.Component;
import org.bukkit.entity.Player;
import org.checkerframework.checker.initialization.qual.UnderInitialization;
import org.checkerframework.checker.nullness.qual.Nullable;

/*
 * 该类负责管理游戏大厅的记分板功能，包含标题、玩家信息、竞技场名称、倒计时等动态内容的显示
 */
public final class LobbyScoreboard {

  // 依赖管理器，提供游戏预启动阶段所需的数据
  private final PreGameManager manager;
  private final LobbySidebarManager boards;

  /**
   * 构造函数初始化依赖关系并创建侧边栏
   * 预游戏阶段管理器，提供玩家、设置等数据
   */
  public LobbyScoreboard(final PreGameManager manager) {
    this.manager = manager;
    this.boards = this.createSidebar(manager);
  }

  /**
   * 将玩家添加到记分板显示系统
   * 要加入的玩家实体
   */
  public void addPlayer(final Player player) {
    this.boards.addPlayer(player);
  }

  /**
   * 初始化侧边栏管理器并绑定现有玩家
   * 预游戏管理器
   * @return 配置完成的侧边栏管理器实例
   */
  public LobbySidebarManager createSidebar(@UnderInitialization LobbyScoreboard this, final PreGameManager manager) {
    final LobbySidebarManager boards = new LobbySidebarManager();
    final PreGamePlayerManager playerManager = manager.getPlayerManager();
    final Collection<Player> participants = playerManager.getParticipants();
    for (final Player player : participants) {
      boards.addPlayer(player);
    }
    return boards;
  }

  /**
   * 关闭记分板系统，清理资源
   */
  public void shutdown() {
    this.boards.delete();
  }

  /**
   * 更新记分板所有显示内容
   * 包含标题更新和内容行的刷新：
   * 1. 空行分隔
   * 2. 竞技场信息
   * 3. 玩家数量
   * 4. 倒计时显示
   * 5. 页脚信息
   */
  public void updateScoreboard() {
    this.boards.updateTitle(this.generateTitleComponent());
    this.boards.updateLines(
        empty(),
        this.generateArenaComponent(),
        this.generatePlayerComponent(),
        empty(),
        this.generateTimerComponent(),
        empty(),
        this.generateFooterComponent()
      );
  }

  /* 记分板标题生成 */
  private Component generateTitleComponent() {
    return Message.LOBBY_SCOREBOARD_TITLE.build();
  }

  /* 页脚信息生成 */
  private Component generateFooterComponent() {
    return Message.LOBBY_SCOREBOARD_DOMAIN.build();
  }

  /* 倒计时显示生成 */
  public Component generateTimerComponent() {
    final PreGamePlayerManager playerManager = this.manager.getPlayerManager();
    final LobbyTimeManager timer = playerManager.getLobbyTimeManager();
    final int time = this.getCurrentTime(timer);
    return Message.LOBBY_SCOREBOARD_TIME.build(time);
  }

  /**
   * 安全获取当前倒计时时间
   *  时间管理器（可能为null）
   *  当前剩余时间（秒），默认返回大厅初始等待时间
   */
  private int getCurrentTime(final @Nullable LobbyTimeManager timer) {
    if (timer == null) {
      return GameProperties.LOBBY_STARTING_TIME;
    } else {
      final LobbyTimer lobbyTimer = timer.getTimer();
      return lobbyTimer == null ? GameProperties.LOBBY_STARTING_TIME : lobbyTimer.getTime();
    }
  }

  /* 玩家数量统计显示 */
  public Component generatePlayerComponent() {
    final PreGamePlayerManager playerManager = this.manager.getPlayerManager();
    final int maxPlayers = playerManager.getMaximumPlayerCount();
    final int currentPlayers = playerManager.getCurrentPlayerCount();
    return Message.LOBBY_SCOREBOARD_PLAYERS.build(currentPlayers, maxPlayers);
  }

  /* 竞技场信息显示 */
  private Component generateArenaComponent() {
    final GameSettings settings = this.manager.getSettings();
    final Arena arena = requireNonNull(settings.getArena()); // 强制要求配置竞技场
    final String name = arena.getName();
    return Message.LOBBY_SCOREBOARD_ARENA.build(name);
  }
}
