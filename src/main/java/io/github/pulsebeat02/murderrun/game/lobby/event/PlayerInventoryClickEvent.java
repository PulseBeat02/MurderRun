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
import io.github.pulsebeat02.murderrun.utils.PDCUtils;
import java.util.Collection;
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
