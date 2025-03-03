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
package io.github.pulsebeat02.murderrun.game.gadget.survivor.utility;

import io.github.pulsebeat02.murderrun.game.Game;
import io.github.pulsebeat02.murderrun.game.GameProperties;
import io.github.pulsebeat02.murderrun.game.gadget.packet.GadgetDropPacket;
import io.github.pulsebeat02.murderrun.game.gadget.survivor.SurvivorGadget;
import io.github.pulsebeat02.murderrun.game.player.GamePlayer;
import io.github.pulsebeat02.murderrun.game.player.PlayerAudience;
import io.github.pulsebeat02.murderrun.game.player.death.PlayerDeathTask;
import io.github.pulsebeat02.murderrun.game.scheduler.GameScheduler;
import io.github.pulsebeat02.murderrun.game.scheduler.reference.PlayerReference;
import io.github.pulsebeat02.murderrun.locale.Message;
import java.util.Objects;
import org.bukkit.Color;
import org.bukkit.GameMode;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.entity.Item;
import org.bukkit.util.Vector;

public final class Horcrux extends SurvivorGadget {

  private static final int COUNTDOWN_SECONDS = 5;
  private static final long TICK_PER_SECOND = 20L;

  public Horcrux() {
    super("horcrux", Material.CHARCOAL, Message.HORCRUX_NAME.build(), Message.HORCRUX_LORE.build(), GameProperties.HORCRUX_COST);
  }

  @Override
  public boolean onGadgetDrop(final GadgetDropPacket packet) {
    final Game game = packet.getGame();
    final GamePlayer player = packet.getPlayer();
    final Item item = packet.getItem();

    // 锁定物品状态
    final Location respawnPoint = item.getLocation().clone();
    item.setVelocity(new Vector(0, 0, 0));
    item.setInvulnerable(true);

    // 注册死亡回调
    player
      .getDeathManager()
      .addDeathTask(
        new PlayerDeathTask(
          () -> {
            // 立即清除物品
            if (item.isValid()) {
              item.remove();
            }

            // 启动复活流程
            startSpectatorRespawn(player, respawnPoint.clone());
          },
          true
        )
      );

    // 粒子效果
    game.getScheduler().scheduleParticleTaskUntilDeath(item, Color.BLACK);

    return false;
  }

  private void startSpectatorRespawn(final GamePlayer player, final Location respawnPoint) {
    final GameScheduler scheduler = player.getGame().getScheduler();
    final PlayerReference ref = PlayerReference.of(player);
    final PlayerAudience audience = player.getAudience();

    // 设为旁观模式
    player.setGameMode(GameMode.SPECTATOR);

    // 阶段1：显示初始提示
    scheduler.scheduleTask(
      () -> {
        audience.sendMessage(Message.HORCRUX_ACTIVATE.build());
        startCountdown(player, respawnPoint);
      },
      20L,
      ref
    ); // 延迟1秒确保死亡动画完成
  }

  private void startCountdown(final GamePlayer player, final Location respawnPoint) {
    final GameScheduler scheduler = player.getGame().getScheduler();
    final PlayerReference ref = PlayerReference.of(player);
    final PlayerAudience audience = player.getAudience();

    // 最终传送阶段
    scheduler.scheduleTask(
      () -> {
        executeFinalRespawn(player, respawnPoint);
      },
      COUNTDOWN_SECONDS * TICK_PER_SECOND,
      ref
    );
  }

  private void executeFinalRespawn(final GamePlayer player, final Location point) {
    final PlayerAudience audience = player.getAudience();

    // 恢复生存模式
    player.setGameMode(GameMode.SURVIVAL);

    // 安全传送
    final Location safeLocation = findSafeLocation(point.clone());
    player.teleport(safeLocation);

    // 设置临时无敌
    player.setInvulnerable(true);
    player
      .getGame()
      .getScheduler()
      .scheduleTask(
        () -> player.setInvulnerable(false),
        60L, // 3秒无敌
        PlayerReference.of(player)
      );
  }

  private Location findSafeLocation(Location loc) {
    // 防止卡在方块中
    if (!loc.getBlock().getType().isAir()) {
      loc.add(0, 1, 0);
      if (!loc.getBlock().getType().isAir()) {
        loc = Objects.requireNonNull(loc.getWorld()).getHighestBlockAt(loc).getLocation().add(0, 1, 0);
      }
    }
    return loc.add(0, 0.5, 0); // 确保站立位置
  }
}
