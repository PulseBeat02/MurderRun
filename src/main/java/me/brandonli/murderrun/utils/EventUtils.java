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
package me.brandonli.murderrun.utils;

import org.bukkit.Location;
import org.bukkit.block.Block;
import org.bukkit.entity.Entity;
import org.bukkit.event.entity.ProjectileHitEvent;
import org.checkerframework.checker.nullness.qual.Nullable;

public final class EventUtils {

  private EventUtils() {
    throw new UnsupportedOperationException("Utility class cannot be instantiated");
  }

  public static @Nullable Location getProjectileLocation(final ProjectileHitEvent event) {
    final Block block = event.getHitBlock();
    final Entity hitEntity = event.getHitEntity();
    return block != null ? block.getLocation() : (hitEntity != null ? hitEntity.getLocation() : null);
  }
}
