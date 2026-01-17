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
package me.brandonli.murderrun.game.ability.survivor;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import me.brandonli.murderrun.game.Game;
import me.brandonli.murderrun.game.GameProperties;
import me.brandonli.murderrun.game.GameStatus;
import me.brandonli.murderrun.game.player.GamePlayer;
import me.brandonli.murderrun.game.player.GamePlayerManager;
import me.brandonli.murderrun.game.player.Killer;
import me.brandonli.murderrun.locale.Message;
import me.brandonli.murderrun.utils.item.ItemFactory;
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
    final GameProperties properties = game.getProperties();
    super(
        game,
        DOUBLE_JUMP_NAME,
        ItemFactory.createAbility(
            DOUBLE_JUMP_NAME,
            Message.DOUBLE_JUMP_NAME.build(),
            Message.DOUBLE_JUMP_LORE.build(),
            (int) (properties.getDoubleJumpCooldown() * 20)));
    this.cooldowns = new ConcurrentHashMap<>();
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

    final GameProperties properties = game.getProperties();
    final double raw = properties.getDoubleJumpCooldown();
    final int cooldown = (int) (raw * 1000);
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
    gamePlayer.setAbilityCooldowns(DOUBLE_JUMP_NAME, (int) (raw * 20));

    final Location location = player.getLocation();
    final Vector direction = location.getDirection();
    final double jumpVelocity = properties.getDoubleJumpVelocity();
    direction.setY(jumpVelocity);
    player.setVelocity(direction);
  }

  @EventHandler(priority = EventPriority.LOWEST)
  public void onPlayerLand(final PlayerMoveEvent event) {
    final Player player = event.getPlayer();
    @SuppressWarnings("deprecation")
    final boolean isOnGround = player.isOnGround();
    if (!isOnGround) {
      return;
    }

    final Game game = this.getGame();
    final GamePlayerManager manager = game.getPlayerManager();
    if (!manager.checkPlayerExists(player)) {
      return;
    }

    final GameStatus status = game.getStatus();
    final GameStatus.Status currentStatus = status.getStatus();
    if (currentStatus == GameStatus.Status.NOT_STARTED
        || currentStatus == GameStatus.Status.FINISHED) {
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
