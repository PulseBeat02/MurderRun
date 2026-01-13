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
package me.brandonli.murderrun.game.gadget.killer.utility.tool;

import me.brandonli.murderrun.game.Game;
import me.brandonli.murderrun.game.GameProperties;
import me.brandonli.murderrun.game.gadget.killer.KillerGadget;
import me.brandonli.murderrun.game.gadget.packet.GadgetDropPacket;
import me.brandonli.murderrun.game.player.GamePlayerManager;
import me.brandonli.murderrun.locale.Message;
import me.brandonli.murderrun.utils.PDCUtils;
import me.brandonli.murderrun.utils.item.ItemFactory;
import org.bukkit.Location;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerFishEvent;
import org.bukkit.event.player.PlayerFishEvent.State;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.PlayerInventory;
import org.bukkit.util.Vector;

public final class Hook extends KillerGadget implements Listener {

  private final Game game;

  public Hook(final Game game) {
    final GameProperties properties = game.getProperties();
    super(
      "hook",
      properties.getHookCost(),
      ItemFactory.createHook(
        ItemFactory.createGadget("hook", properties.getHookMaterial(), Message.HOOK_NAME.build(), Message.HOOK_LORE.build())
      )
    );
    this.game = game;
  }

  @Override
  public boolean onGadgetDrop(final GadgetDropPacket packet) {
    return true;
  }

  @EventHandler(priority = EventPriority.LOWEST)
  public void onPlayerFish(final PlayerFishEvent event) {
    final State state = event.getState();
    if (state != State.CAUGHT_ENTITY) {
      return;
    }

    final Entity caught = event.getCaught();
    if (caught == null) {
      return;
    }

    if (!(caught instanceof final Player player)) {
      return;
    }

    final GamePlayerManager manager = this.game.getPlayerManager();
    if (!manager.checkPlayerExists(player)) {
      return;
    }

    final Player killer = event.getPlayer();
    final PlayerInventory inventory = killer.getInventory();
    final ItemStack hand = inventory.getItemInMainHand();
    if (!PDCUtils.isHook(hand)) {
      return;
    }

    final Vector multiplied = this.getMultipliedVelocity(killer, caught);
    caught.setVelocity(multiplied);
  }

  private Vector getMultipliedVelocity(final Player killer, final Entity caught) {
    final Location killerLocation = killer.getLocation();
    final Location caughtLocation = caught.getLocation();
    final Vector killerVector = killerLocation.toVector();
    final Vector caughtVector = caughtLocation.toVector();
    final Vector pullVector = killerVector.subtract(caughtVector);
    final Vector normalized = pullVector.normalize();
    return normalized.multiply(2);
  }
}
