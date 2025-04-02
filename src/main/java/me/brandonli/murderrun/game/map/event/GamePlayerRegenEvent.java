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
package me.brandonli.murderrun.game.map.event;

import java.util.Set;
import me.brandonli.murderrun.game.Game;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.entity.EntityRegainHealthEvent;

public final class GamePlayerRegenEvent extends GameEvent {

  private static final Set<EntityRegainHealthEvent.RegainReason> REASONS = Set.of(
    EntityRegainHealthEvent.RegainReason.SATIATED,
    EntityRegainHealthEvent.RegainReason.REGEN,
    EntityRegainHealthEvent.RegainReason.EATING
  );

  public GamePlayerRegenEvent(final Game game) {
    super(game);
  }

  @EventHandler(priority = EventPriority.LOWEST)
  private void onPlayerRegenEvent(final EntityRegainHealthEvent event) {
    final Entity entity = event.getEntity();
    if (!(entity instanceof final Player player)) {
      return;
    }

    if (!this.isGamePlayer(player)) {
      return;
    }

    final EntityRegainHealthEvent.RegainReason reason = event.getRegainReason();
    if (REASONS.contains(reason)) {
      event.setCancelled(true);
    }
  }
}
