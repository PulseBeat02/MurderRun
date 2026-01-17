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
import me.brandonli.murderrun.utils.PDCUtils;
import me.brandonli.murderrun.utils.item.ItemFactory;
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
    final GameProperties properties = game.getProperties();
    super(
        game,
        CANNON_NAME,
        ItemFactory.createAbility(
            CANNON_NAME, Message.CANNON_NAME.build(), Message.CANNON_LORE.build(), (int)
                (properties.getCannonCooldown() * 20)));
    this.cooldowns = new ConcurrentHashMap<>();
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

    final GameProperties properties = game.getProperties();
    final double raw = properties.getCannonCooldown();
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

    final long current = System.currentTimeMillis();
    this.cooldowns.put(gamePlayer, current);
    gamePlayer.setAbilityCooldowns(CANNON_NAME, (int) (raw * 20));

    final double multiplier = properties.getCannonVelocity();
    final int ticks = properties.getCannonFuse();
    final Location location = player.getEyeLocation();
    final Vector direction = location.getDirection();
    final Vector normalizedDirection = direction.normalize();
    final Vector velocity = normalizedDirection.multiply(multiplier);
    final TNTPrimed tnt = player.getWorld().spawn(location, TNTPrimed.class);
    tnt.setVelocity(velocity);
    tnt.setFuseTicks(ticks);
  }
}
