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
package me.brandonli.murderrun.game.scheduler.reference;

import org.bukkit.entity.Entity;
import org.bukkit.entity.FallingBlock;

public final class FallingBlockReference extends EntityReference {

  FallingBlockReference(final FallingBlock entity) {
    super(entity);
  }

  public static FallingBlockReference of(final FallingBlock entity) {
    return new FallingBlockReference(entity);
  }

  @Override
  public boolean isInvalid() {
    final Entity entity = this.get();
    return entity.isOnGround();
  }
}
