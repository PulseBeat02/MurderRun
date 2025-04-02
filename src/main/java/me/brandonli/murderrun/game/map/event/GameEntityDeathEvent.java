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

import static java.util.Objects.requireNonNull;

import java.util.List;
import me.brandonli.murderrun.game.Game;
import me.brandonli.murderrun.game.GameSettings;
import me.brandonli.murderrun.game.arena.Arena;
import org.bukkit.Location;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.entity.EntityDeathEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.util.BoundingBox;
import org.bukkit.util.Vector;

public final class GameEntityDeathEvent extends GameEvent {

  public GameEntityDeathEvent(final Game game) {
    super(game);
  }

  @EventHandler(priority = EventPriority.LOWEST)
  public void onEntityDeath(final EntityDeathEvent event) {
    final Game game = this.getGame();
    final GameSettings settings = game.getSettings();
    final Arena arena = requireNonNull(settings.getArena());
    final BoundingBox boundingBox = arena.createBox();
    final LivingEntity entity = event.getEntity();
    final Location location = entity.getLocation();
    final Vector vector = location.toVector();
    if (!boundingBox.contains(vector)) {
      return;
    }

    if (entity instanceof Player) {
      return;
    }

    final List<ItemStack> stacks = event.getDrops();
    stacks.clear();
    event.setDroppedExp(0);
  }
}
