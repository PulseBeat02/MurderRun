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
import java.util.HashMap;
import java.util.Map;
import java.util.Map.Entry;
import me.brandonli.murderrun.game.Game;
import me.brandonli.murderrun.game.GameProperties;
import me.brandonli.murderrun.game.gadget.killer.KillerGadget;
import me.brandonli.murderrun.game.gadget.packet.GadgetDropPacket;
import me.brandonli.murderrun.game.player.GamePlayer;
import me.brandonli.murderrun.game.player.PlayerAudience;
import me.brandonli.murderrun.game.scheduler.GameScheduler;
import me.brandonli.murderrun.game.scheduler.reference.NullReference;
import me.brandonli.murderrun.game.scheduler.reference.StrictPlayerReference;
import me.brandonli.murderrun.locale.Message;
import me.brandonli.murderrun.utils.item.ItemFactory;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.entity.Item;
import org.checkerframework.checker.nullness.qual.KeyFor;

public final class IcePath extends KillerGadget {

  public IcePath(final Game game) {
    final GameProperties properties = game.getProperties();
    super(
        "ice_path",
        properties.getIcePathCost(),
        ItemFactory.createGadget(
            "ice_path",
            properties.getIcePathMaterial(),
            Message.ICE_PATH_NAME.build(),
            Message.ICE_PATH_LORE.build()));
  }

  @Override
  public boolean onGadgetDrop(final GadgetDropPacket packet) {
    final Game game = packet.getGame();
    final GamePlayer player = packet.getPlayer();
    final Item item = packet.getItem();
    item.remove();

    final StrictPlayerReference reference = StrictPlayerReference.of(player);
    final GameScheduler scheduler = game.getScheduler();
    scheduler.scheduleRepeatedTask(() -> this.setIceTrail(game, player), 0, 4, 20 * 60L, reference);

    final GameProperties properties = game.getProperties();
    final PlayerAudience audience = player.getAudience();
    audience.playSound(properties.getIcePathSound());

    return false;
  }

  private void setIceTrail(final Game game, final GamePlayer player) {
    final Location location = player.getLocation();
    final Map<Location, Material> originalBlocks = new HashMap<>();
    for (int x = -1; x <= 1; x++) {
      for (int z = -1; z <= 1; z++) {
        final Location clone = location.clone();
        final Location blockLocation = clone.add(x, -1, z);
        final Block block = blockLocation.getBlock();
        final Material type = block.getType();
        if (!type.equals(Material.ICE)) {
          originalBlocks.put(blockLocation, type);
          block.setType(Material.ICE);
        }
      }
    }

    final Map<Location, Material> blocksToRestore = new HashMap<>(originalBlocks);
    final GameScheduler scheduler = game.getScheduler();
    final NullReference reference = NullReference.of();
    scheduler.scheduleTask(
        () -> {
          final Collection<Entry<@KeyFor("blocksToRestore") Location, Material>> entries =
              blocksToRestore.entrySet();
          for (final Map.Entry<Location, Material> entry : entries) {
            final Location blockLocation = entry.getKey();
            final Block block = blockLocation.getBlock();
            final Material material = entry.getValue();
            block.setType(material);
            block.getState().update(true);
          }
        },
        2 * 20L,
        reference);
  }
}
