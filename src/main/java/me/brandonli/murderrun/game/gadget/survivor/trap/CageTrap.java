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
package me.brandonli.murderrun.game.gadget.survivor.trap;

import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;
import me.brandonli.murderrun.game.Game;
import me.brandonli.murderrun.game.GameProperties;
import me.brandonli.murderrun.game.player.GamePlayer;
import me.brandonli.murderrun.game.player.GamePlayerManager;
import me.brandonli.murderrun.game.scheduler.GameScheduler;
import me.brandonli.murderrun.game.scheduler.reference.NullReference;
import me.brandonli.murderrun.locale.Message;
import me.brandonli.murderrun.utils.item.ItemFactory;
import me.brandonli.murderrun.utils.map.MapUtils;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.block.BlockFace;
import org.bukkit.entity.Item;
import org.incendo.cloud.type.tuple.Triplet;

public final class CageTrap extends SurvivorTrap {

  private static final Set<BlockFace> FACES = Set.of(BlockFace.DOWN, BlockFace.EAST, BlockFace.WEST, BlockFace.NORTH, BlockFace.SOUTH);
  private static final List<Triplet<Integer, Integer, Integer>> CAGE_TRAP_VECTORS;

  static {
    CAGE_TRAP_VECTORS = FACES.stream().map(MapUtils::toPosTriplet).collect(Collectors.toList());
    CAGE_TRAP_VECTORS.add(Triplet.of(0, 2, 0));
  }

  public CageTrap(final Game game) {
    final GameProperties properties = game.getProperties();
    super(
      "cage_trap",
      properties.getCageCost(),
      ItemFactory.createGadget("cage_trap", properties.getCageMaterial(), Message.CAGE_NAME.build(), Message.CAGE_LORE.build()),
      Message.CAGE_ACTIVATE.build(),
      properties.getCageColor()
    );
  }

  @Override
  public void onTrapActivate(final Game game, final GamePlayer murderer, final Item item) {
    final Location location = murderer.getLocation();
    final Block block = location.getBlock();
    final Block[] blocks = this.getBlocksInOrder(block);
    final Material[] history = this.getBlockHistory(blocks);

    final NullReference reference = NullReference.of();
    final GameScheduler scheduler = game.getScheduler();
    final Runnable task = () -> this.resetBlocks(history, blocks);
    final GameProperties properties = game.getProperties();
    scheduler.scheduleTask(task, properties.getCageDuration(), reference);

    final GamePlayerManager manager = game.getPlayerManager();
    manager.playSoundForAllParticipants(properties.getCageSound());
  }

  private Block[] getBlocksInOrder(final Block origin) {
    final int length = CAGE_TRAP_VECTORS.size();
    final Block[] blocks = new Block[length];
    for (int i = 0; i < length; i++) {
      final Triplet<Integer, Integer, Integer> direction = CAGE_TRAP_VECTORS.get(i);
      final int x = direction.first();
      final int y = direction.second();
      final int z = direction.third();
      final Block block = origin.getRelative(x, y, z);
      blocks[i] = block;
    }
    return blocks;
  }

  private void resetBlocks(final Material[] history, final Block[] blocks) {
    final int length = history.length;
    for (int i = 0; i < length; i++) {
      final Material material = history[i];
      final Block block = blocks[i];
      block.setType(material);
    }
  }

  private Material[] getBlockHistory(final Block[] blocks) {
    final int length = blocks.length;
    final Material[] original = new Material[length];
    for (int i = 0; i < length; i++) {
      final Block block = blocks[i];
      final Material type = block.getType();
      original[i] = type;
      block.setType(Material.BEDROCK);
    }
    return original;
  }
}
