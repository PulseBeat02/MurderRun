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
package io.github.pulsebeat02.murderrun.game.ability.survivor;

import io.github.pulsebeat02.murderrun.game.Game;
import io.github.pulsebeat02.murderrun.game.GameProperties;
import io.github.pulsebeat02.murderrun.game.GameStatus;
import io.github.pulsebeat02.murderrun.game.player.GamePlayer;
import io.github.pulsebeat02.murderrun.game.player.GamePlayerManager;
import io.github.pulsebeat02.murderrun.game.player.Killer;
import io.github.pulsebeat02.murderrun.locale.Message;
import io.github.pulsebeat02.murderrun.utils.item.ItemFactory;
import java.util.HashMap;
import java.util.Map;
import org.bukkit.Location;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerMoveEvent;
import org.bukkit.event.player.PlayerToggleFlightEvent;
import org.bukkit.util.Vector;

public final class DoubleJump extends SurvivorAbility implements Listener {

  private static final String DOUBLE_JUMP_NAME = "double_jump";

  private final Map<GamePlayer, Long> cooldowns;

  public DoubleJump(final Game game) {
    super(
      game,
      DOUBLE_JUMP_NAME,
      ItemFactory.createAbility(
        DOUBLE_JUMP_NAME,
        Message.DOUBLE_JUMP_NAME.build(),
        Message.DOUBLE_JUMP_LORE.build(),
        (int) (GameProperties.DOUBLEJUMP_COOLDOWN * 20)
      )
    );
    this.cooldowns = new HashMap<>();
  }

  @EventHandler(priority = EventPriority.LOWEST)
  public void onPlayerToggleFlight(final PlayerToggleFlightEvent event) {
    final Game game = this.getGame();
    final GamePlayerManager manager = game.getPlayerManager();
    final Player player = event.getPlayer();
    if (!manager.checkPlayerExists(player)) {
      return;
    }

    final GameStatus status = game.getStatus();
    final GameStatus.Status currentStatus = status.getStatus();
    if (currentStatus == GameStatus.Status.NOT_STARTED) {
      return;
    }

    final GamePlayer gamePlayer = manager.getGamePlayer(player);
    final boolean killer = gamePlayer instanceof Killer;
    if (currentStatus == GameStatus.Status.SURVIVORS_RELEASED && killer) {
      return;
    }

    if (!gamePlayer.hasAbility(DOUBLE_JUMP_NAME)) {
      return;
    }

    if (this.invokeEvent(gamePlayer)) {
      return;
    }

    final int cooldown = (int) (GameProperties.DOUBLEJUMP_COOLDOWN * 1000);
    if (this.cooldowns.containsKey(gamePlayer)) {
      final long last = this.cooldowns.get(gamePlayer);
      final long current = System.currentTimeMillis();
      final long timeElapsed = current - last;
      if (timeElapsed < cooldown) {
        event.setCancelled(true);
        return;
      }
    }

    if (player.isFlying()) {
      return;
    }
    event.setCancelled(true);
    player.setAllowFlight(false);

    final long current = System.currentTimeMillis();
    this.cooldowns.put(gamePlayer, current);
    gamePlayer.setAbilityCooldowns(DOUBLE_JUMP_NAME, (int) (GameProperties.DOUBLEJUMP_COOLDOWN * 20));

    final Location location = player.getLocation();
    final Vector direction = location.getDirection();
    final double jumpVelocity = GameProperties.DOUBLEJUMP_VELOCITY;
    direction.setY(jumpVelocity);
    player.setVelocity(direction);
  }

  @EventHandler(priority = EventPriority.LOWEST)
  @SuppressWarnings("deprecation")
  public void onPlayerLand(final PlayerMoveEvent event) {
    final Player player = event.getPlayer();
    if (!player.isOnGround()) {
      return;
    }

    final Game game = this.getGame();
    final GamePlayerManager manager = game.getPlayerManager();
    if (!manager.checkPlayerExists(player)) {
      return;
    }

    final GameStatus status = game.getStatus();
    final GameStatus.Status currentStatus = status.getStatus();
    if (currentStatus == GameStatus.Status.NOT_STARTED || currentStatus == GameStatus.Status.FINISHED) {
      return;
    }

    final GamePlayer gamePlayer = manager.getGamePlayer(player);
    final boolean killer = gamePlayer instanceof Killer;
    if (currentStatus == GameStatus.Status.SURVIVORS_RELEASED && killer) {
      return;
    }

    if (!gamePlayer.hasAbility(DOUBLE_JUMP_NAME)) {
      return;
    }

    player.setAllowFlight(true);
  }
}
