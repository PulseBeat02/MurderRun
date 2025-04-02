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
import me.brandonli.murderrun.game.GameSettings;
import me.brandonli.murderrun.game.arena.Arena;
import me.brandonli.murderrun.game.gadget.packet.GadgetDropPacket;
import me.brandonli.murderrun.game.gadget.survivor.SurvivorGadget;
import me.brandonli.murderrun.game.player.GamePlayer;
import me.brandonli.murderrun.game.player.PlayerAudience;
import me.brandonli.murderrun.locale.Message;
import me.brandonli.murderrun.utils.item.ItemFactory;
import me.brandonli.murderrun.utils.map.MapUtils;
import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.entity.Item;

public final class RandomTeleport extends SurvivorGadget {

  public RandomTeleport() {
    super(
      "random_teleport",
      GameProperties.RANDOM_TELEPORT_COST,
      ItemFactory.createGadget(
        "random_teleport",
        GameProperties.RANDOM_TELEPORT_MATERIAL,
        Message.TP_ME_AWAY_FROM_HERE_NAME.build(),
        Message.TP_ME_AWAY_FROM_HERE_LORE.build()
      )
    );
  }

  @Override
  public boolean onGadgetDrop(final GadgetDropPacket packet) {
    final Game game = packet.getGame();
    final GamePlayer player = packet.getPlayer();
    final Item item = packet.getItem();
    item.remove();

    final GameSettings settings = game.getSettings();
    final Arena arena = requireNonNull(settings.getArena());
    final Location first = arena.getFirstCorner();
    final Location second = arena.getSecondCorner();
    final double[] coords = MapUtils.generateFriendlyRandomXZ(first, second);
    final World world = requireNonNull(first.getWorld());
    final Location temp = new Location(world, coords[0], 0, coords[1]);
    final Location teleport = MapUtils.getHighestSpawnLocation(temp);
    player.teleport(teleport);

    final PlayerAudience audience = player.getAudience();
    audience.playSound(GameProperties.RANDOM_TELEPORT_SOUND);

    return false;
  }
}
