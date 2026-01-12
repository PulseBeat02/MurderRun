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
import me.brandonli.murderrun.game.gadget.killer.KillerGadget;
import me.brandonli.murderrun.game.gadget.packet.GadgetDropPacket;
import me.brandonli.murderrun.game.player.GamePlayer;
import me.brandonli.murderrun.game.player.GamePlayerManager;
import me.brandonli.murderrun.game.player.PlayerAudience;
import me.brandonli.murderrun.locale.Message;
import me.brandonli.murderrun.utils.item.ItemFactory;
import net.kyori.adventure.text.Component;
import org.bukkit.Location;
import org.bukkit.entity.Item;

public final class MurderousWarp extends KillerGadget {

  public MurderousWarp(final Game game) {
    final GameProperties properties = game.getProperties();
    super(
      "murderous_warp",
      properties.getMurderousWarpCost(),
      ItemFactory.createGadget(
        "murderous_warp",
        properties.getMurderousWarpMaterial(),
        Message.MURDEROUS_WARP_NAME.build(),
        Message.MURDEROUS_WARP_LORE.build()
      )
    );
  }

  @Override
  public boolean onGadgetDrop(final GadgetDropPacket packet) {
    final Game game = packet.getGame();
    final GamePlayer player = packet.getPlayer();
    final Item item = packet.getItem();
    item.remove();

    final GamePlayerManager manager = game.getPlayerManager();
    final GamePlayer random = manager.getRandomAliveInnocentPlayer();
    final Location first = random.getLocation();
    final Location second = player.getLocation();
    random.teleport(second);
    player.teleport(first);

    final GameProperties properties = game.getProperties();
    final PlayerAudience audienceRand = random.getAudience();
    audienceRand.playSound(properties.getMurderousWarpSound());

    final Component msg = Message.WARP_DISTORT_ACTIVATE.build();
    audienceRand.sendMessage(msg);

    final PlayerAudience audienceKiller = player.getAudience();
    audienceKiller.sendMessage(msg);

    return false;
  }
}
