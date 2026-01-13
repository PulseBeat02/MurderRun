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

import java.util.Optional;
import java.util.UUID;
import me.brandonli.murderrun.game.Game;
import me.brandonli.murderrun.game.GameMode;
import me.brandonli.murderrun.game.map.event.GameEvent;
import me.brandonli.murderrun.game.player.GamePlayer;
import me.brandonli.murderrun.game.player.GamePlayerManager;
import me.brandonli.murderrun.game.player.Survivor;
import org.bukkit.Location;
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
    final Optional<Survivor> nearbyFrozen = this.findNearbyFrozenSurvivor(playerLoc);
    if (nearbyFrozen.isEmpty()) {
      if (!isSneaking && survivor.getRevivingPlayer() != null) {
        this.findRevivingTarget(survivor).ifPresent(this.freezeTagManager::stopRevive);
      }
      return;
    }

    final Survivor nearbyFrozenSurvivor = nearbyFrozen.get();
    final UUID reviving = nearbyFrozenSurvivor.getRevivingPlayer();
    final UUID survivorUUID = survivor.getUUID();

    if (isSneaking) {
      if (reviving == null) {
        this.freezeTagManager.startRevive(nearbyFrozenSurvivor, survivor);
      }
    } else {
      if (reviving != null && reviving.equals(survivorUUID)) {
        this.freezeTagManager.stopRevive(nearbyFrozenSurvivor);
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

  private Optional<Survivor> findNearbyFrozenSurvivor(final Location location) {
    final Game game = this.getGame();
    final GamePlayerManager manager = game.getPlayerManager();
    return manager
      .getSurvivors()
      .filter(s -> s instanceof Survivor)
      .map(s -> (Survivor) s)
      .filter(Survivor::isFrozen)
      .filter(s -> this.freezeTagManager.isNearCorpse(location, s))
      .findFirst();
  }
}
