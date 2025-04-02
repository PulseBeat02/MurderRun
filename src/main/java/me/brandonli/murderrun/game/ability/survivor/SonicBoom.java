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

import java.util.Collection;
import java.util.HashMap;
import java.util.Map;
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
import org.bukkit.Particle;
import org.bukkit.World;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.util.Vector;

public final class SonicBoom extends SurvivorAbility implements Listener {

  private static final String SONIC_BOOM_NAME = "sonic_boom";

  private final Map<GamePlayer, Long> cooldowns;

  public SonicBoom(final Game game) {
    super(
      game,
      SONIC_BOOM_NAME,
      ItemFactory.createAbility(
        SONIC_BOOM_NAME,
        Message.SONIC_BOOM_NAME.build(),
        Message.SONIC_BOOM_LORE.build(),
        (int) (GameProperties.SONIC_BOOM_COOLDOWN * 20)
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

    if (!gamePlayer.hasAbility(SONIC_BOOM_NAME)) {
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

    final int cooldown = (int) (GameProperties.SONIC_BOOM_COOLDOWN * 1000);
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
    gamePlayer.setAbilityCooldowns(SONIC_BOOM_NAME, (int) (GameProperties.SONIC_BOOM_COOLDOWN * 20));

    final double radius = GameProperties.SONIC_BOOM_RADIUS;
    final double knockbackStrength = GameProperties.SONIC_BOOM_KNOCKBACK;
    final Location sourceLocation = player.getLocation();
    final Vector knockbackDirection = sourceLocation.getDirection().normalize();
    final World world = player.getWorld();
    final Vector velocity = knockbackDirection.multiply(knockbackStrength);

    final Collection<Entity> entities = world.getNearbyEntities(sourceLocation, radius, radius, radius);
    entities
      .stream()
      .filter(entity -> !entity.equals(player))
      .forEach(entity -> this.launchPlayers(entity, sourceLocation, knockbackDirection, velocity));
    world.spawnParticle(Particle.SONIC_BOOM, sourceLocation, 50);
  }

  private void launchPlayers(final Entity entity, final Location sourceLocation, final Vector knockbackDirection, final Vector velocity) {
    final Location targetLocation = entity.getLocation();
    final Vector vector = targetLocation.toVector();
    final Vector sourceVector = sourceLocation.toVector();
    final Vector subtract = vector.subtract(sourceVector);
    final Vector toTarget = subtract.normalize();
    final double dotProduct = knockbackDirection.dot(toTarget);
    if (dotProduct > 0) {
      entity.setVelocity(velocity);
    }
  }
}
