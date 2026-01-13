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
import me.brandonli.murderrun.game.gadget.survivor.SurvivorGadget;
import me.brandonli.murderrun.game.map.BlockWhitelistManager;
import me.brandonli.murderrun.game.map.GameMap;
import me.brandonli.murderrun.game.player.GamePlayer;
import me.brandonli.murderrun.game.player.PlayerAudience;
import me.brandonli.murderrun.locale.Message;
import me.brandonli.murderrun.utils.item.ItemFactory;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.World;
import org.bukkit.block.Block;
import org.bukkit.entity.Item;

public final class CryoFreeze extends SurvivorGadget {

  public CryoFreeze(final Game game) {
    final GameProperties properties = game.getProperties();
    super(
      "cryo_freeze",
      properties.getCryoFreezeCost(),
      ItemFactory.createGadget(
        "cryo_freeze",
        properties.getCryoFreezeMaterial(),
        Message.CRYO_FREEZE_NAME.build(),
        Message.CRYO_FREEZE_LORE.build()
      )
    );
  }

  @Override
  public boolean onGadgetDrop(final GadgetDropPacket packet) {
    final Game game = packet.getGame();
    final GamePlayer player = packet.getPlayer();
    final Item item = packet.getItem();
    item.remove();

    final Location location = player.getLocation();
    final World world = requireNonNull(location.getWorld());
    final GameMap map = game.getMap();
    final BlockWhitelistManager whitelistManager = map.getBlockWhitelistManager();
    final int cx = location.getBlockX();
    final int cy = location.getBlockY();
    final int cz = location.getBlockZ();

    final GameProperties properties = game.getProperties();
    final int radius = properties.getCryoFreezeRadius();
    for (int x = -radius; x <= radius; x++) {
      for (int y = 0; y <= radius; y++) {
        for (int z = -radius; z <= radius; z++) {
          final double distance = Math.sqrt((double) x * x + (double) y * y + (double) z * z);
          if (distance >= radius - 0.5 && distance <= radius + 0.5) {
            final Block block = world.getBlockAt(cx + x, cy + y, cz + z);
            block.setType(Material.PACKED_ICE);
            whitelistManager.addWhitelistedBlock(block);
          }
        }
      }
    }

    final PlayerAudience audience = player.getAudience();
    audience.playSound(properties.getCryoFreezeSound());

    return false;
  }
}
