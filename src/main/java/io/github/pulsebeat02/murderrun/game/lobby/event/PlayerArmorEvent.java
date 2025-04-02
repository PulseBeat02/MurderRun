/*

MIT License

Copyright (c) 2025 Brandon Li

Permission is hereby granted, free of charge, to any person obtaining a copy
of this software and associated documentation files (the "Software"), to deal
in the Software without restriction, including without limitation the rights
to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
copies of the Software, and to permit persons to whom the Software is
furnished to do so, subject to the following conditions:

The above copyright notice and this permission notice shall be included in all
copies or substantial portions of the Software.

THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE
SOFTWARE.

*/
package io.github.pulsebeat02.murderrun.game.lobby.event;

import io.github.pulsebeat02.murderrun.game.lobby.PreGameManager;
import io.github.pulsebeat02.murderrun.game.lobby.PreGamePlayerManager;
import java.util.Collection;
import org.bukkit.entity.HumanEntity;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.inventory.InventoryType;
import org.bukkit.inventory.Inventory;

public final class PlayerArmorEvent implements Listener {

  private final PreGameManager manager;

  public PlayerArmorEvent(final PreGameManager manager) {
    this.manager = manager;
  }

  @EventHandler(priority = EventPriority.LOWEST)
  public void onInventoryClick(final InventoryClickEvent event) {
    final Inventory inventory = event.getClickedInventory();
    if (inventory == null) {
      return;
    }

    final InventoryType type = inventory.getType();
    if (type != InventoryType.PLAYER) {
      return;
    }

    final InventoryType.SlotType slotType = event.getSlotType();
    if (slotType != InventoryType.SlotType.ARMOR) {
      return;
    }

    final HumanEntity whoClicked = event.getWhoClicked();
    if (!(whoClicked instanceof final Player player)) {
      return;
    }

    final PreGamePlayerManager playerManager = this.manager.getPlayerManager();
    final Collection<Player> participants = playerManager.getMurderers();
    if (!participants.contains(player)) {
      return;
    }

    event.setCancelled(true);
  }
}
