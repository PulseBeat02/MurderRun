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

import io.github.pulsebeat02.murderrun.game.Game;
import io.github.pulsebeat02.murderrun.game.GameResult;
import java.util.Collection;
import java.util.HashSet;

/*
 * 负责统一管理所有进行中的游戏实例，提供强制终止和生命周期管理功能
 */
public final class GameShutdownManager {

  // 使用HashSet存储当前活动的游戏实例，实现快速查找和去重
  private final Collection<Game> currentGames;

  /**
   * 构造函数初始化游戏容器
   * 使用HashSet保证：
   * 1. 游戏实例唯一性（同一游戏不会被重复添加）
   * 2. O(1)时间复杂度的添加/删除操作
   */
  public GameShutdownManager() {
    this.currentGames = new HashSet<>();
  }

  /**
   * 紧急终止所有进行中的游戏
   * 典型应用场景：
   * - 服务器关闭时
   * - 插件重载时
   * - 系统错误发生时
   */
  public void forceShutdown() {
    // 遍历所有游戏实例并标记为中断状态
    for (final Game game : this.currentGames) {
      game.finishGame(GameResult.INTERRUPTED);
    }
  }

  /**
   * 注册新启动的游戏实例到管理系统
   * 需要被监控的游戏对象
   */
  public void addGame(final Game game) {
    this.currentGames.add(game);
  }

  /**
   * 从管理系统移除已结束的游戏
   * 已完成生命周期管理的游戏对象
   */
  public void removeGame(final Game game) {
    this.currentGames.remove(game);
  }
}
