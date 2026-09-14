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

import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;
import me.brandonli.murderrun.game.player.GamePlayer;
import me.brandonli.murderrun.game.scheduler.GameScheduler;
import me.brandonli.murderrun.game.scheduler.reference.OnlinePlayerReference;
import me.brandonli.murderrun.utils.item.Item;
import org.bukkit.Material;
import org.bukkit.inventory.EquipmentSlot;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.PlayerInventory;
import org.checkerframework.checker.nullness.qual.Nullable;

public final class JumpScare {

  private final Set<GamePlayer> currentlyJumpScared;

  public JumpScare() {
    this.currentlyJumpScared = ConcurrentHashMap.newKeySet();
  }

  public void apply(final GamePlayer player, final GameScheduler scheduler, final long duration) {
    if (this.currentlyJumpScared.contains(player)) {
      return;
    }
    this.setPumpkinItemStack(player);
    final OnlinePlayerReference reference = OnlinePlayerReference.of(player);
    scheduler.scheduleTask(
        () -> this.setBackHelmet(player),
        duration,
        reference,
        () -> this.currentlyJumpScared.remove(player));
    this.currentlyJumpScared.add(player);
  }

  private void setBackHelmet(final GamePlayer player) {
    final PlayerInventory inventory = player.getInventory();
    final @Nullable ItemStack helmet = inventory.getHelmet();
    final ItemStack actual = helmet == null ? Item.AIR_STACK : helmet;
    player.sendEquipmentChange(EquipmentSlot.HEAD, actual);
    this.currentlyJumpScared.remove(player);
  }

  private void setPumpkinItemStack(final GamePlayer player) {
    final ItemStack stack = Item.create(Material.CARVED_PUMPKIN);
    player.sendEquipmentChange(EquipmentSlot.HEAD, stack);
  }
}
