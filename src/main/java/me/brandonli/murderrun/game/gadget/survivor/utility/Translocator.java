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
package me.brandonli.murderrun.game.gadget.survivor.utility;

import static java.util.Objects.requireNonNull;

import me.brandonli.murderrun.game.Game;
import me.brandonli.murderrun.game.GameProperties;
import me.brandonli.murderrun.game.gadget.packet.GadgetDropPacket;
import me.brandonli.murderrun.game.gadget.packet.GadgetRightClickPacket;
import me.brandonli.murderrun.game.gadget.survivor.SurvivorGadget;
import me.brandonli.murderrun.game.player.GamePlayer;
import me.brandonli.murderrun.game.player.PlayerAudience;
import me.brandonli.murderrun.locale.Message;
import me.brandonli.murderrun.utils.InventoryUtils;
import me.brandonli.murderrun.utils.PDCUtils;
import me.brandonli.murderrun.utils.immutable.Keys;
import me.brandonli.murderrun.utils.item.Item;
import me.brandonli.murderrun.utils.item.ItemFactory;
import me.brandonli.murderrun.utils.map.MapUtils;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.PlayerInventory;
import org.bukkit.persistence.PersistentDataType;

public final class Translocator extends SurvivorGadget {

  public Translocator(final Game game) {
    final GameProperties properties = game.getProperties();
    super(
      "translocator",
      properties.getTranslocatorCost(),
      ItemFactory.createTranslocator(
        ItemFactory.createGadget(
          "translocator",
          properties.getTranslocatorMaterial(),
          Message.TRANSLOCATOR_NAME.build(),
          Message.TRANSLOCATOR_LORE.build()
        )
      )
    );
  }

  @Override
  public boolean onGadgetRightClick(final GadgetRightClickPacket packet) {
    final GamePlayer player = packet.getPlayer();
    final ItemStack stack = packet.getItemStack();
    if (stack == null) {
      return true;
    }

    final Material material = stack.getType();
    if (material != Material.LEVER) {
      return true;
    }

    final byte[] data = requireNonNull(PDCUtils.getPersistentDataAttribute(stack, Keys.TRANSLOCATOR, PersistentDataType.BYTE_ARRAY));
    final Location location = MapUtils.byteArrayToLocation(data);
    player.teleport(location);

    final PlayerInventory inventory = player.getInventory();
    InventoryUtils.consumeStack(inventory, stack);

    final Game game = player.getGame();
    final GameProperties properties = game.getProperties();
    final PlayerAudience audience = player.getAudience();
    audience.playSound(properties.getTranslocatorSound());

    return false;
  }

  @Override
  public boolean onGadgetDrop(final GadgetDropPacket packet) {
    final GamePlayer player = packet.getPlayer();
    final org.bukkit.entity.Item item = packet.getItem();

    final Location location = player.getLocation();
    final ItemStack stack = item.getItemStack();
    final byte[] bytes = MapUtils.locationToByteArray(location);
    Item.builder(stack)
      .lore(Message.TRANSLOCATOR_LORE1.build())
      .pdc(Keys.TRANSLOCATOR, PersistentDataType.BYTE_ARRAY, bytes)
      .model(null)
      .type(Material.LEVER);

    return true;
  }
}
