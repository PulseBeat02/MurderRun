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

import java.util.Collection;
import java.util.List;
import java.util.Map;
import me.brandonli.murderrun.game.Game;
import me.brandonli.murderrun.game.GameProperties;
import me.brandonli.murderrun.game.gadget.killer.KillerGadget;
import me.brandonli.murderrun.game.gadget.packet.GadgetDropPacket;
import me.brandonli.murderrun.game.map.GameMap;
import me.brandonli.murderrun.game.map.part.CarPart;
import me.brandonli.murderrun.game.map.part.PartsManager;
import me.brandonli.murderrun.game.player.GamePlayer;
import me.brandonli.murderrun.game.player.PlayerAudience;
import me.brandonli.murderrun.locale.Message;
import me.brandonli.murderrun.utils.StreamUtils;
import me.brandonli.murderrun.utils.item.ItemFactory;
import org.bukkit.Location;
import org.bukkit.entity.Item;

public final class PartWarp extends KillerGadget {

  public PartWarp(final Game game) {
    final GameProperties properties = game.getProperties();
    super(
      "part_warp",
      properties.getPartWarpCost(),
      ItemFactory.createGadget(
        "part_warp",
        properties.getPartWarpMaterial(),
        Message.PART_WARP_NAME.build(),
        Message.PART_WARP_LORE.build()
      )
    );
  }

  @Override
  public boolean onGadgetDrop(final GadgetDropPacket packet) {
    final Game game = packet.getGame();
    final GamePlayer player = packet.getPlayer();
    final Item item = packet.getItem();
    item.remove();

    final GameMap map = game.getMap();
    final PartsManager manager = map.getCarPartManager();
    final Map<String, CarPart> parts = manager.getParts();
    final Collection<CarPart> values = parts.values();
    final List<CarPart> shuffled = values.stream().collect(StreamUtils.toShuffledList());
    final CarPart part = this.getRandomCarPart(shuffled);
    final Item carPartItem = part.getItem();

    final Location location = player.getLocation();
    carPartItem.teleport(location);

    final GameProperties properties = game.getProperties();
    final PlayerAudience audience = player.getAudience();
    audience.playSound(properties.getPartWarpSound());

    return false;
  }

  public CarPart getRandomCarPart(final List<CarPart> shuffled) {
    if (shuffled.isEmpty()) {
      throw new IllegalStateException("No car parts available to warp");
    }
    CarPart chosen = shuffled.getFirst();
    while (chosen.isPickedUp()) {
      shuffled.remove(chosen);
      if (shuffled.isEmpty()) {
        throw new IllegalStateException("All car parts are already picked up");
      }
      chosen = shuffled.getFirst();
    }
    return chosen;
  }
}
