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
package me.brandonli.murderrun.game.gadget.misc;

import java.util.UUID;
import me.brandonli.murderrun.game.Game;
import me.brandonli.murderrun.game.player.GamePlayer;
import me.brandonli.murderrun.game.player.GamePlayerManager;
import org.bukkit.Location;
import org.bukkit.entity.Mob;
import org.bukkit.event.entity.EntityTargetEvent;

public interface TargetableEntity {
  default void handle(
      final EntityTargetEvent event, final String target, final Mob entity, final boolean killer) {
    final Game game = this.getGame();
    final UUID uuid = UUID.fromString(target);
    final GamePlayerManager manager = game.getPlayerManager();
    if (!manager.checkPlayerExists(uuid)) {
      entity.remove();
      return;
    }

    final Location location = entity.getLocation();
    final GamePlayer nearest;
    if (killer) {
      nearest = manager.getNearestKiller(location);
    } else {
      nearest = manager.getNearestLivingSurvivor(location);
    }

    if (nearest == null) {
      entity.remove();
      return;
    }

    event.setCancelled(true);

    nearest.apply(entity::setTarget);
  }

  Game getGame();
}
