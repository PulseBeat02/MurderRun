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

import me.brandonli.murderrun.game.Game;
import me.brandonli.murderrun.game.map.GameMap;
import me.brandonli.murderrun.game.map.part.CarPart;
import me.brandonli.murderrun.game.map.part.PartsManager;
import me.brandonli.murderrun.game.player.GamePlayer;
import me.brandonli.murderrun.game.player.GamePlayerManager;
import me.brandonli.murderrun.game.player.Killer;
import me.brandonli.murderrun.game.player.Survivor;
import me.brandonli.murderrun.utils.PDCUtils;
import org.bukkit.entity.Item;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.entity.EntityPickupItemEvent;
import org.bukkit.inventory.ItemStack;

public final class GamePlayerPickupEvent extends GameEvent {

  public GamePlayerPickupEvent(final Game game) {
    super(game);
  }

  @EventHandler(priority = EventPriority.LOWEST)
  public void onPlayerPickupItem(final EntityPickupItemEvent event) {
    final LivingEntity entity = event.getEntity();
    if (!(entity instanceof final Player player)) {
      return;
    }

    if (!this.isGamePlayer(player)) {
      return;
    }

    final Game game = this.getGame();
    final GamePlayerManager playerManager = game.getPlayerManager();
    final Item item = event.getItem();
    final ItemStack stack = item.getItemStack();
    final boolean isCarPart = PDCUtils.isCarPart(stack);
    final boolean isTrap = PDCUtils.isTrap(stack);
    if (!(isCarPart || isTrap)) {
      return;
    }

    if (isTrap) {
      event.setCancelled(true);
      return;
    }

    final GamePlayer gamePlayer = playerManager.getGamePlayer(player);
    if (gamePlayer instanceof Killer) {
      event.setCancelled(true);
      return;
    }

    final Survivor survivor = (Survivor) gamePlayer;
    if (!survivor.canPickupCarPart()) {
      event.setCancelled(true);
      return;
    }
    survivor.setHasCarPart(true);

    final GameMap map = game.getMap();
    final PartsManager manager = map.getCarPartManager();
    final CarPart carPart = manager.getCarPartItemStack(stack);
    if (carPart != null) {
      carPart.setPickedUp(true);
    }
  }
}
