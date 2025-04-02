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

import java.util.HashSet;
import java.util.Set;
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
import org.bukkit.Material;
import org.bukkit.World;
import org.bukkit.block.Block;
import org.bukkit.entity.Item;

public final class RandomTeleport extends SurvivorGadget {

  private static final Set<Material> BLACKLISTED_BLOCKS;

  static {
    BLACKLISTED_BLOCKS = new HashSet<>();
    final String raw = GameProperties.RANDOM_TELEPORT_BLACKLISTED_BLOCKS;
    final String[] individual = raw.split(",");
    for (final String material : individual) {
      final String upper = material.toUpperCase();
      final Material target = Material.getMaterial(upper);
      if (target != null) {
        BLACKLISTED_BLOCKS.add(Material.valueOf(material));
      }
    }
  }

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

    Location teleport = null;
    final int maxAttempts = 100;
    int attempts = 0;

    while (teleport == null && attempts < maxAttempts) {
      final double[] coords = MapUtils.generateFriendlyRandomXZ(first, second);
      final World world = requireNonNull(first.getWorld());
      final Location temp = new Location(world, coords[0], 0, coords[1]);
      final Location potentialTeleport = MapUtils.getHighestSpawnLocation(temp);
      if (this.isSafeLocation(potentialTeleport)) {
        teleport = potentialTeleport;
      }
      attempts++;
    }

    if (teleport != null) {
      player.teleport(teleport);
      final PlayerAudience audience = player.getAudience();
      audience.playSound(GameProperties.RANDOM_TELEPORT_SOUND);
    }

    return false;
  }

  private boolean isSafeLocation(final Location location) {
    if (location == null) {
      return false;
    }

    final World world = location.getWorld();
    if (world == null) {
      return false;
    }

    final Block block = world.getBlockAt(location);
    final Material type = block.getType();
    if (BLACKLISTED_BLOCKS.contains(type)) {
      return false;
    }

    final double y = location.getY();
    return y > 0;
  }
}
