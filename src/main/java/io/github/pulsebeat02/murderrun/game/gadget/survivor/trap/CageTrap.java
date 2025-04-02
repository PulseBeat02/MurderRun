/*

MIT License

Copyright (c) 2024 Brandon Li

Permission is hereby granted, free of charge, to any person obtaining a copy
of this software and associated documentation files (the "Software"), to deal
in the Software without restriction, including without limitation the rights
to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
copies of the Software, and to permit persons to whom the Software is
furnished to do so, subject to the following conditions:

The above copyright notice and this permission notice shall be included in all
copies or substantial portions of the Software.

THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE
SOFTWARE.

*/
package io.github.pulsebeat02.murderrun.game.gadget.survivor.trap;

import io.github.pulsebeat02.murderrun.game.Game;
import io.github.pulsebeat02.murderrun.game.GameProperties;
import io.github.pulsebeat02.murderrun.game.player.GamePlayer;
import io.github.pulsebeat02.murderrun.game.player.GamePlayerManager;
import io.github.pulsebeat02.murderrun.game.scheduler.GameScheduler;
import io.github.pulsebeat02.murderrun.game.scheduler.reference.NullReference;
import io.github.pulsebeat02.murderrun.locale.Message;
import io.github.pulsebeat02.murderrun.utils.item.ItemFactory;
import io.github.pulsebeat02.murderrun.utils.map.MapUtils;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.block.BlockFace;
import org.bukkit.entity.Item;
import org.incendo.cloud.type.tuple.Triplet;

public final class CageTrap extends SurvivorTrap {

  private static final Set<BlockFace> faces = Set.of(BlockFace.DOWN, BlockFace.EAST, BlockFace.WEST, BlockFace.NORTH, BlockFace.SOUTH);
  private static final List<Triplet<Integer, Integer, Integer>> CAGE_TRAP_VECTORS;

  static {
    CAGE_TRAP_VECTORS = faces.stream().map(MapUtils::toPosTriplet).collect(Collectors.toList());
    CAGE_TRAP_VECTORS.add(Triplet.of(0, 2, 0));
  }

  public CageTrap() {
    super(
      "cage_trap",
      GameProperties.CAGE_COST,
      ItemFactory.createGadget("cage_trap", GameProperties.CAGE_MATERIAL, Message.CAGE_NAME.build(), Message.CAGE_LORE.build()),
      Message.CAGE_ACTIVATE.build(),
      GameProperties.CAGE_COLOR
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
    scheduler.scheduleTask(task, GameProperties.CAGE_DURATION, reference);

    final GamePlayerManager manager = game.getPlayerManager();
    manager.playSoundForAllParticipants(GameProperties.CAGE_SOUND);
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
