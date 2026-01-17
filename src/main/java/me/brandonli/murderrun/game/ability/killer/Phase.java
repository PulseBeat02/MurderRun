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
package me.brandonli.murderrun.game.ability.killer;

import java.util.Collection;
import java.util.HashSet;
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
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.util.Vector;
import org.checkerframework.checker.nullness.qual.Nullable;

public final class Phase extends KillerAbility implements Listener {

  private static final String PHASE_NAME = "phase";

  private final Collection<Material> blacklisted;
  private final Map<GamePlayer, Long> cooldowns;

  public Phase(final Game game) {
    final GameProperties properties = game.getProperties();
    super(
        game,
        PHASE_NAME,
        ItemFactory.createAbility(
            PHASE_NAME, Message.PHASE_NAME.build(), Message.PHASE_LORE.build(), (int)
                (properties.getPhaseCooldown() * 20)));
    this.blacklisted = new HashSet<>();
    final String raw = properties.getPhaseBlacklistedBlocks();
    final String[] individual = raw.split(",");
    for (final String material : individual) {
      final String upper = material.toUpperCase();
      final Material target = Material.getMaterial(upper);
      if (target != null) {
        this.blacklisted.add(Material.valueOf(material));
      }
    }
    this.cooldowns = new ConcurrentHashMap<>();
  }

  @EventHandler(priority = EventPriority.LOWEST)
  public void onPlayerRightClick(final PlayerInteractEvent event) {
    final Action action = event.getAction();
    if (action != Action.RIGHT_CLICK_AIR && action != Action.RIGHT_CLICK_BLOCK) {
      return;
    }

    final Game game = this.getGame();
    final GamePlayerManager manager = game.getPlayerManager();
    final Player player = event.getPlayer();
    if (!manager.checkPlayerExists(player)) {
      return;
    }

    final GamePlayer gamePlayer = manager.getGamePlayer(player);
    final GameStatus status = game.getStatus();
    final GameStatus.Status currentStatus = status.getStatus();
    if (currentStatus == GameStatus.Status.NOT_STARTED) {
      return;
    }

    final ItemStack item = event.getItem();
    if (item == null) {
      return;
    }

    if (!gamePlayer.hasAbility(PHASE_NAME)) {
      return;
    }

    if (!PDCUtils.isAbility(item)) {
      return;
    }

    final boolean killer = gamePlayer instanceof Killer;
    if (currentStatus == GameStatus.Status.SURVIVORS_RELEASED && killer) {
      player.setCooldown(item, 0);
      event.setCancelled(true);
      return;
    }

    if (this.invokeEvent(gamePlayer)) {
      return;
    }

    final Location eyeLocation = player.getEyeLocation();
    final Vector eyeDirection = eyeLocation.getDirection();
    final Vector direction = eyeDirection.normalize();
    final GameProperties properties = game.getProperties();
    final double maxDistance = properties.getPhaseDistance();
    final Location targetLocation = this.getLocation(maxDistance, eyeLocation, direction);
    if (targetLocation != null) {
      final boolean isPathClear = this.isIsPathClear(maxDistance, eyeLocation, direction);
      if (isPathClear) {
        final boolean canPhase = this.isCanPhase(targetLocation);
        if (canPhase) {
          if (this.setCooldown(event, gamePlayer)) {
            return;
          }
          player.teleport(targetLocation);

          final long current = System.currentTimeMillis();
          this.cooldowns.put(gamePlayer, current);
          gamePlayer.setAbilityCooldowns(PHASE_NAME, (int) (properties.getPhaseCooldown() * 20));
        }
      }
    }
  }

  private boolean setCooldown(final PlayerInteractEvent event, final GamePlayer gamePlayer) {
    final Game game = this.getGame();
    final GameProperties properties = game.getProperties();
    final int cooldown = (int) (properties.getPhaseCooldown() * 1000);
    if (this.cooldowns.containsKey(gamePlayer)) {
      final long last = this.cooldowns.get(gamePlayer);
      final long current = System.currentTimeMillis();
      final long timeElapsed = current - last;
      if (timeElapsed < cooldown) {
        event.setCancelled(true);
        return true;
      }
    }
    return false;
  }

  private boolean isCanPhase(final Location targetLocation) {
    final Location feetLocation = targetLocation.clone();
    final Location headLocation = feetLocation.clone().add(0, 1, 0);
    final Location belowFeetLocation = feetLocation.clone().add(0, -1, 0);

    final Block feetBlock = feetLocation.getBlock();
    final Block headBlock = headLocation.getBlock();
    final Block belowFeetBlock = belowFeetLocation.getBlock();

    final Material feetType = feetBlock.getType();
    final Material headType = headBlock.getType();
    final Material belowFeetType = belowFeetBlock.getType();
    return !feetType.isSolid() && !headType.isSolid() && belowFeetType.isSolid();
  }

  private boolean isIsPathClear(
      final double maxDistance, final Location eyeLocation, final Vector direction) {
    boolean isPathClear = true;
    for (int i = 1; i <= maxDistance; i++) {
      final Location clone = eyeLocation.clone();
      final Vector cloneDirection = direction.clone();
      final Vector multiply = cloneDirection.multiply(i);
      final Location checkLocation = clone.add(multiply);
      final Block checkBlock = checkLocation.getBlock();
      final Material checkType = checkBlock.getType();
      if (this.blacklisted.contains(checkType)) {
        isPathClear = false;
        break;
      }
    }
    return isPathClear;
  }

  private @Nullable Location getLocation(
      final double maxDistance, final Location eyeLocation, final Vector direction) {
    Location targetLocation = null;
    for (int i = 1; i <= maxDistance; i++) {
      final Location clone = eyeLocation.clone();
      final Vector cloneDirection = direction.clone();
      final Vector multiply = cloneDirection.multiply(i);
      final Location checkLocation = clone.add(multiply);
      final Location clone2 = eyeLocation.clone();
      final Vector cloneDirection2 = direction.clone();
      final Vector multiply2 = cloneDirection2.multiply(i + 1);
      final Location nextLocation = clone2.add(multiply2);
      final Block checkBlock = checkLocation.getBlock();
      final Block nextBlock = nextLocation.getBlock();
      final Material checkType = checkBlock.getType();
      final Material nextType = nextBlock.getType();
      if (checkType.isSolid() && !nextType.isSolid()) {
        targetLocation = nextLocation;
        break;
      }
    }
    return targetLocation;
  }
}
