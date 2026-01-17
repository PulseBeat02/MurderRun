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
package me.brandonli.murderrun.game.gadget.killer.utility;

import me.brandonli.murderrun.game.Game;
import me.brandonli.murderrun.game.GameProperties;
import me.brandonli.murderrun.game.extension.GameExtensionManager;
import me.brandonli.murderrun.game.extension.libsdiguises.DisguiseManager;
import me.brandonli.murderrun.game.gadget.killer.KillerGadget;
import me.brandonli.murderrun.game.gadget.packet.GadgetDropPacket;
import me.brandonli.murderrun.game.player.GamePlayer;
import me.brandonli.murderrun.game.player.GamePlayerManager;
import me.brandonli.murderrun.game.player.PlayerAudience;
import me.brandonli.murderrun.locale.Message;
import me.brandonli.murderrun.utils.item.ItemFactory;
import net.kyori.adventure.text.Component;
import org.bukkit.entity.Item;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.PlayerInventory;
import org.checkerframework.checker.nullness.qual.Nullable;

public final class Mimic extends KillerGadget {

  public Mimic(final Game game) {
    final GameProperties properties = game.getProperties();
    super(
        "mimic",
        properties.getMimicCost(),
        ItemFactory.createGadget(
            "mimic",
            properties.getMimicMaterial(),
            Message.MIMIC_NAME.build(),
            Message.MIMIC_LORE.build()));
  }

  @Override
  public boolean onGadgetDrop(final GadgetDropPacket packet) {
    final Game game = packet.getGame();
    final GamePlayer player = packet.getPlayer();
    final Item item = packet.getItem();
    item.remove();

    final GamePlayerManager manager = game.getPlayerManager();
    final GamePlayer survivor = manager.getRandomAliveInnocentPlayer();
    final GameExtensionManager extensionManager = game.getExtensionManager();
    final DisguiseManager disguiseManager = extensionManager.getDisguiseManager();
    disguiseManager.disguisePlayerAsOtherPlayer(player, survivor);

    final PlayerInventory otherInventory = survivor.getInventory();
    final @Nullable ItemStack[] armor = otherInventory.getArmorContents();
    final PlayerInventory thisInventory = player.getInventory();
    thisInventory.setArmorContents(armor);

    final Component msg = Message.MIMIC_ACTIVATE.build();
    final PlayerAudience audience = player.getAudience();
    audience.sendMessage(msg);

    return false;
  }
}
