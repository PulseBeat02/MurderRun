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
package me.brandonli.murderrun.game.lobby.event;

import java.util.Collection;
import me.brandonli.murderrun.game.lobby.PreGameManager;
import me.brandonli.murderrun.game.lobby.PreGamePlayerManager;
import me.brandonli.murderrun.utils.PDCUtils;
import org.bukkit.entity.HumanEntity;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.inventory.ItemStack;

public final class PlayerInventoryClickEvent implements Listener {

  private final PreGameManager manager;

  public PlayerInventoryClickEvent(final PreGameManager manager) {
    this.manager = manager;
  }

  @EventHandler(priority = EventPriority.LOWEST)
  public void onInventoryClick(final InventoryClickEvent event) {
    final HumanEntity entity = event.getWhoClicked();
    if (!(entity instanceof final Player player)) {
      return;
    }

    final PreGamePlayerManager playerManager = this.manager.getPlayerManager();
    final Collection<Player> participants = playerManager.getParticipants();
    if (!participants.contains(player)) {
      return;
    }

    final ItemStack item = event.getCurrentItem();
    if (item == null) {
      return;
    }

    if (!PDCUtils.isAbility(item)) {
      return;
    }

    final int slot = event.getSlot();
    if (slot != 8) {
      return;
    }

    event.setCancelled(true);
  }
}
