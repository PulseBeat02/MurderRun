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
package me.brandonli.murderrun.game.freezetag;

import static java.util.Objects.requireNonNull;

import java.util.Optional;
import java.util.UUID;
import me.brandonli.murderrun.game.Game;
import me.brandonli.murderrun.game.GameMode;
import me.brandonli.murderrun.game.GameProperties;
import me.brandonli.murderrun.game.map.event.GameEvent;
import me.brandonli.murderrun.game.player.GamePlayer;
import me.brandonli.murderrun.game.player.GamePlayerManager;
import me.brandonli.murderrun.game.player.Survivor;
import me.brandonli.murderrun.game.player.death.DeathManager;
import me.brandonli.murderrun.game.scheduler.GameScheduler;
import me.brandonli.murderrun.game.scheduler.reference.LoosePlayerReference;
import net.citizensnpcs.api.npc.NPC;
import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.player.PlayerToggleSneakEvent;

public final class FreezeTagReviveEvent extends GameEvent {

  private final FreezeTagManager freezeTagManager;

  public FreezeTagReviveEvent(final Game game) {
    super(game);
    this.freezeTagManager = game.getFreezeTagManager();
  }

  @EventHandler
  public void onPlayerSneak(final PlayerToggleSneakEvent event) {
    final Player player = event.getPlayer();
    if (!this.isGamePlayer(player)) {
      return;
    }

    final Game game = this.getGame();
    final GameMode mode = game.getMode();
    if (mode != GameMode.FREEZE_TAG) {
      return;
    }

    final GamePlayerManager manager = game.getPlayerManager();
    final GamePlayer gamePlayer = manager.getGamePlayer(player);
    if (!(gamePlayer instanceof final Survivor survivor)) {
      return;
    }

    if (!survivor.isAlive()) {
      return;
    }

    if (survivor.isFrozen()) {
      return;
    }

    final boolean isSneaking = event.isSneaking();
    final Location playerLoc = player.getLocation();
    final Survivor nearbyFrozen = this.findNearbyFrozenSurvivor(playerLoc);
    if (nearbyFrozen == null) {
      if (!isSneaking && survivor.getRevivingPlayer() != null) {
        this.findRevivingTarget(survivor).ifPresent(this.freezeTagManager::stopRevive);
      }
      return;
    }

    final UUID reviving = nearbyFrozen.getRevivingPlayer();
    if (isSneaking) {
      if (reviving == null) {
        this.freezeTagManager.startRevive(nearbyFrozen, survivor);
      }
      final GameScheduler scheduler = game.getScheduler();
      final LoosePlayerReference reference = LoosePlayerReference.of(survivor);
      scheduler.scheduleTask(
        () -> {
          if (player.isSneaking() && this.isNearCorpse(player.getLocation(), nearbyFrozen)) {
            this.freezeTagManager.updateRevive(nearbyFrozen);
          }
        },
        10L,
        reference
      );
    } else {
      final UUID survivorUUID = survivor.getUUID();
      if (reviving != null && reviving.equals(survivorUUID)) {
        this.freezeTagManager.stopRevive(nearbyFrozen);
      }
    }
  }

  private Optional<Survivor> findRevivingTarget(final Survivor survivor) {
    final Game game = this.getGame();
    final GamePlayerManager manager = game.getPlayerManager();
    return manager
      .getSurvivors()
      .filter(s -> s instanceof Survivor)
      .map(s -> (Survivor) s)
      .filter(Survivor::isFrozen)
      .filter(s -> survivor.getUUID().equals(s.getRevivingPlayer()))
      .findFirst();
  }

  private Survivor findNearbyFrozenSurvivor(final Location location) {
    final Game game = this.getGame();
    final GamePlayerManager manager = game.getPlayerManager();
    return manager
      .getSurvivors()
      .filter(s -> s instanceof Survivor)
      .map(s -> (Survivor) s)
      .filter(Survivor::isFrozen)
      .filter(s -> this.isNearCorpse(location, s))
      .findFirst()
      .orElse(null);
  }

  private boolean isNearCorpse(final Location playerLoc, final Survivor frozen) {
    final DeathManager deathManager = frozen.getDeathManager();
    final NPC corpse = deathManager.getCorpse();
    if (corpse == null) {
      return false;
    }

    final Entity entity = corpse.getEntity();
    if (entity == null || entity.isDead()) {
      return false;
    }

    final Location corpseLoc = entity.getLocation();
    final World playerWorld = requireNonNull(playerLoc.getWorld());
    final World corpseWorld = corpseLoc.getWorld();
    if (!playerWorld.equals(corpseWorld)) {
      return false;
    }

    final Game game = this.freezeTagManager.getGame();
    final GameProperties properties = game.getProperties();
    final double reviveDistance = properties.getFreezeTagReviveRadius();
    final double reviveDistanceSquared = reviveDistance * reviveDistance;
    return playerLoc.distanceSquared(corpseLoc) <= reviveDistanceSquared;
  }
}
