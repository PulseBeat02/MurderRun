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
package me.brandonli.murderrun.game.gadget.survivor.trap;

import me.brandonli.murderrun.game.Game;
import me.brandonli.murderrun.game.GameProperties;
import me.brandonli.murderrun.game.player.GamePlayer;
import me.brandonli.murderrun.game.player.GamePlayerManager;
import me.brandonli.murderrun.game.scheduler.GameScheduler;
import me.brandonli.murderrun.game.scheduler.reference.StrictPlayerReference;
import me.brandonli.murderrun.locale.Message;
import me.brandonli.murderrun.utils.PDCUtils;
import me.brandonli.murderrun.utils.item.ItemFactory;
import org.bukkit.entity.Item;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.PlayerInventory;
import org.checkerframework.checker.nullness.qual.Nullable;

public final class HackTrap extends SurvivorTrap {

  public HackTrap(final Game game) {
    final GameProperties properties = game.getProperties();
    super(
      "hack_trap",
      properties.getHackCost(),
      ItemFactory.createGadget("hack_trap", properties.getHackMaterial(), Message.HACK_NAME.build(), Message.HACK_LORE.build()),
      Message.HACK_ACTIVATE.build(),
      properties.getHackColor()
    );
  }

  @Override
  public void onTrapActivate(final Game game, final GamePlayer murderer, final Item item) {
    final PlayerInventory inventory = murderer.getInventory();
    final ItemStack stack = this.getSword(inventory);
    if (stack == null) {
      return;
    }

    final StrictPlayerReference reference = StrictPlayerReference.of(murderer);
    final GameScheduler scheduler = game.getScheduler();
    final GameProperties properties = game.getProperties();
    scheduler.scheduleTask(() -> inventory.addItem(stack), properties.getHackDuration(), reference);

    final GamePlayerManager manager = game.getPlayerManager();
    manager.playSoundForAllParticipants(properties.getHackSound());
  }

  private @Nullable ItemStack getSword(final PlayerInventory inventory) {
    @SuppressWarnings("all") // checker
    final ItemStack[] slots = inventory.getContents();
    ItemStack find = null;
    for (final ItemStack stack : slots) {
      if (PDCUtils.isSword(stack)) {
        find = stack;
        inventory.remove(find);
        break;
      }
    }
    return find;
  }
}
