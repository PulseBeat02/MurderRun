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

import me.brandonli.murderrun.game.Game;
import me.brandonli.murderrun.game.GameProperties;
import me.brandonli.murderrun.game.gadget.packet.GadgetDropPacket;
import me.brandonli.murderrun.game.gadget.survivor.SurvivorGadget;
import me.brandonli.murderrun.game.player.GamePlayer;
import me.brandonli.murderrun.game.player.PlayerAudience;
import me.brandonli.murderrun.game.scheduler.GameScheduler;
import me.brandonli.murderrun.game.scheduler.reference.StrictPlayerReference;
import me.brandonli.murderrun.locale.Message;
import me.brandonli.murderrun.utils.item.ItemFactory;
import org.bukkit.GameMode;
import org.bukkit.Location;
import org.bukkit.entity.Item;

public final class Drone extends SurvivorGadget {

  public Drone(final Game game) {
    final GameProperties properties = game.getProperties();
    super(
      "drone",
      properties.getDroneCost(),
      ItemFactory.createGadget("drone", properties.getDroneMaterial(), Message.DRONE_NAME.build(), Message.DRONE_LORE.build())
    );
  }

  @Override
  public boolean onGadgetDrop(final GadgetDropPacket packet) {
    final Game game = packet.getGame();
    final GamePlayer player = packet.getPlayer();
    final Item item = packet.getItem();
    item.remove();

    final Location origin = player.getLocation();
    final Location clone = origin.clone();
    clone.add(0, 20, 0);

    player.setGameMode(GameMode.SPECTATOR);
    player.teleport(clone);

    final GameProperties properties = game.getProperties();
    final GameScheduler scheduler = game.getScheduler();
    final StrictPlayerReference reference = StrictPlayerReference.of(player);
    scheduler.scheduleTask(() -> this.resetPlayer(player, origin), properties.getDroneDuration(), reference);

    final PlayerAudience audience = player.getAudience();
    audience.playSound(properties.getDroneSound());

    return false;
  }

  private void resetPlayer(final GamePlayer player, final Location origin) {
    player.teleport(origin);
    player.setGameMode(GameMode.SURVIVAL);
  }
}
