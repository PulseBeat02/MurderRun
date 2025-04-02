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
package me.brandonli.murderrun.utils.screen;

import java.util.Objects;
import java.util.Set;
import org.bukkit.Location;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.CreatureSpawnEvent;
import org.bukkit.event.player.PlayerTeleportEvent;

public final class ScreenListener implements Listener {

  private final Set<Player> tracked;

  public ScreenListener(final Set<Player> tracked) {
    this.tracked = tracked;
  }

  @EventHandler(priority = EventPriority.LOWEST)
  public void onCreatureSpawnEvent(final CreatureSpawnEvent event) {
    final Location eventLocation = event.getLocation();
    for (final Player player : this.tracked) {
      final Location playerLocation = player.getLocation();
      if (!this.equalLocations(eventLocation, playerLocation)) {
        continue;
      }
      event.setCancelled(true);
      return;
    }
  }

  @EventHandler(priority = EventPriority.LOWEST)
  public void onPlayerTeleportEvent(final PlayerTeleportEvent event) {
    final Location eventLocation = event.getTo();
    if (eventLocation == null) {
      return;
    }
    for (final Player player : this.tracked) {
      final Location playerLocation = player.getLocation();
      if (!this.equalLocations(eventLocation, playerLocation)) {
        continue;
      }
      event.setCancelled(true);
      return;
    }
  }

  private boolean equalLocations(final Location loc1, final Location loc2) {
    return (
      Objects.equals(loc1.getWorld(), loc2.getWorld()) &&
      loc1.getX() == loc2.getX() &&
      loc1.getY() == loc2.getY() &&
      loc1.getZ() == loc2.getZ()
    );
  }
}
