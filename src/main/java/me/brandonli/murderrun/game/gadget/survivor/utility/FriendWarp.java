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

import java.util.stream.Stream;
import me.brandonli.murderrun.game.Game;
import me.brandonli.murderrun.game.GameProperties;
import me.brandonli.murderrun.game.gadget.packet.GadgetDropPacket;
import me.brandonli.murderrun.game.gadget.survivor.SurvivorGadget;
import me.brandonli.murderrun.game.player.GamePlayer;
import me.brandonli.murderrun.game.player.GamePlayerManager;
import me.brandonli.murderrun.game.player.PlayerAudience;
import me.brandonli.murderrun.locale.Message;
import me.brandonli.murderrun.utils.item.ItemFactory;
import org.bukkit.Location;
import org.bukkit.entity.Item;

public final class FriendWarp extends SurvivorGadget {

  public FriendWarp(final Game game) {
    final GameProperties properties = game.getProperties();
    super(
      "friend_warp",
      properties.getFriendWarpCost(),
      ItemFactory.createGadget(
        "friend_warp",
        properties.getFriendWarpMaterial(),
        Message.FRIEND_WARP_NAME.build(),
        Message.FRIEND_WARP_LORE.build()
      )
    );
  }

  @Override
  public boolean onGadgetDrop(final GadgetDropPacket packet) {
    final Game game = packet.getGame();
    final GamePlayer player = packet.getPlayer();
    final Item item = packet.getItem();

    final GamePlayerManager manager = game.getPlayerManager();
    final Stream<GamePlayer> survivors = manager.getLivingInnocentPlayers();
    final long size = survivors.count();
    if (size < 2) {
      return true;
    }
    item.remove();

    final GamePlayer target = this.getRandomSurvivorNotSame(manager, player);
    final Location location = target.getLocation();
    player.teleport(location);

    final GameProperties properties = game.getProperties();
    final String sound = properties.getFriendWarpSound();
    final PlayerAudience audience = player.getAudience();
    audience.playSound(sound);

    final PlayerAudience targetAudience = target.getAudience();
    targetAudience.playSound(sound);

    return false;
  }

  private GamePlayer getRandomSurvivorNotSame(final GamePlayerManager manager, final GamePlayer gamePlayer) {
    GamePlayer random = manager.getRandomAliveInnocentPlayer();
    while (random == gamePlayer) {
      random = manager.getRandomAliveInnocentPlayer();
    }
    return random;
  }
}
