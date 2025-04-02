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
import io.github.pulsebeat02.murderrun.utils.PDCUtils;
import io.github.pulsebeat02.murderrun.utils.item.ItemFactory;
import java.util.HashMap;
import java.util.Map;
import org.bukkit.Location;
import org.bukkit.entity.Player;
import org.bukkit.entity.TNTPrimed;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.util.Vector;

public final class Cannon extends SurvivorAbility implements Listener {

  private static final String CANNON_NAME = "cannon";

  private final Map<GamePlayer, Long> cooldowns;

  public Cannon(final Game game) {
    super(
      game,
      CANNON_NAME,
      ItemFactory.createAbility(
        CANNON_NAME,
        Message.CANNON_NAME.build(),
        Message.CANNON_LORE.build(),
        (int) (GameProperties.CANNON_COOLDOWN * 20)
      )
    );
    this.cooldowns = new HashMap<>();
  }

  @EventHandler(priority = EventPriority.LOWEST)
  public void onPlayerRightClick(final PlayerInteractEvent event) {
    final Action action = event.getAction();
    if (action != Action.RIGHT_CLICK_AIR) {
      return;
    }

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

    if (!gamePlayer.hasAbility(CANNON_NAME)) {
      return;
    }

    final ItemStack item = event.getItem();
    if (item == null) {
      return;
    }

    if (!PDCUtils.isAbility(item)) {
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

    final long current = System.currentTimeMillis();
    this.cooldowns.put(gamePlayer, current);
    gamePlayer.setAbilityCooldowns(CANNON_NAME, (int) (GameProperties.DOUBLEJUMP_COOLDOWN * 20));

    final double multiplier = GameProperties.CANNON_VELOCITY;
    final int ticks = GameProperties.CANNON_FUSE;
    final Location location = player.getEyeLocation();
    final Vector direction = location.getDirection();
    final Vector normalizedDirection = direction.normalize();
    final Vector velocity = normalizedDirection.multiply(multiplier);
    final TNTPrimed tnt = player.getWorld().spawn(location, TNTPrimed.class);
    tnt.setVelocity(velocity);
    tnt.setFuseTicks(ticks);
  }
}
