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
import io.github.pulsebeat02.murderrun.game.player.GamePlayer;
import io.github.pulsebeat02.murderrun.game.player.GamePlayerManager;
import io.github.pulsebeat02.murderrun.locale.Message;
import io.github.pulsebeat02.murderrun.utils.item.ItemFactory;
import java.util.HashMap;
import java.util.Map;
import org.bukkit.Location;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.player.PlayerMoveEvent;
import org.bukkit.event.player.PlayerToggleFlightEvent;
import org.bukkit.util.Vector;

public final class DoubleJumpAbility extends SurvivorAbility {

  private static final String DOUBLE_JUMP_NAME = "double_jump";

  private final Game game;
  private final Map<GamePlayer, Long> cooldowns;

  public DoubleJumpAbility(final Game game) {
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
    this.game = game;
    this.cooldowns = new HashMap<>();
  }

  @EventHandler(priority = EventPriority.LOWEST)
  public void onPlayerToggleFlight(final PlayerToggleFlightEvent event) {
    final GamePlayerManager manager = this.game.getPlayerManager();
    final Player player = event.getPlayer();
    if (!manager.checkPlayerExists(player)) {
      return;
    }

    final GamePlayer gamePlayer = manager.getGamePlayer(player);
    if (!gamePlayer.hasAbility(DOUBLE_JUMP_NAME)) {
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

    final Location location = player.getLocation();
    final Vector direction = location.getDirection();
    final double jumpVelocity = GameProperties.DOUBLEJUMP_VELOCITY;
    direction.setY(jumpVelocity);
    player.setVelocity(direction);

    gamePlayer.setAbilityCooldowns(DOUBLE_JUMP_NAME, (int) (GameProperties.DOUBLEJUMP_COOLDOWN * 20));
  }

  @EventHandler(priority = EventPriority.LOWEST)
  @SuppressWarnings("deprecation")
  public void onPlayerLand(final PlayerMoveEvent event) {
    final Player player = event.getPlayer();
    if (!player.isOnGround()) {
      return;
    }

    final GamePlayerManager manager = this.game.getPlayerManager();
    if (!manager.checkPlayerExists(player)) {
      return;
    }

    final GamePlayer gamePlayer = manager.getGamePlayer(player);
    if (!gamePlayer.hasAbility(DOUBLE_JUMP_NAME)) {
      return;
    }

    player.setAllowFlight(true);
  }
}
