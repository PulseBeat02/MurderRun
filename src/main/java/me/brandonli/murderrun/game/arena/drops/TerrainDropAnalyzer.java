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
package me.brandonli.murderrun.game.arena.drops;

import static java.util.Objects.requireNonNull;

import com.sk89q.worldedit.EditSession;
import com.sk89q.worldedit.WorldEdit;
import com.sk89q.worldedit.bukkit.BukkitAdapter;
import com.sk89q.worldedit.math.BlockVector3;
import com.sk89q.worldedit.world.block.BlockState;
import com.sk89q.worldedit.world.block.BlockType;
import com.sk89q.worldedit.world.registry.BlockMaterial;
import java.util.*;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ConcurrentLinkedQueue;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import me.brandonli.murderrun.MurderRun;
import me.brandonli.murderrun.game.GameProperties;
import me.brandonli.murderrun.utils.ExecutorUtils;
import me.brandonli.murderrun.utils.RandomUtils;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.World;
import org.bukkit.scheduler.BukkitRunnable;

public final class TerrainDropAnalyzer {

  private static final Set<BlockType> DOOR_TYPES;

  static {
    DOOR_TYPES = new HashSet<>();
    final Material[] values = Material.values();
    for (final Material material : values) {
      final String name = material.name();
      if (!name.contains("DOOR")) {
        continue;
      }
      final BlockType type = BukkitAdapter.asBlockType(material);
      if (type != null) {
        DOOR_TYPES.add(type);
      }
    }
  }

  public static void init() {
    // init
  }

  private final MurderRun plugin;
  private final Location[] corners;
  private final Location spawn;

  public TerrainDropAnalyzer(final MurderRun plugin, final Location[] corners, final Location spawn) {
    this.plugin = plugin;
    this.corners = corners;
    this.spawn = spawn;
  }

  public CompletableFuture<Location[]> getRandomDrops() {
    final CompletableFuture<List<BlockVector3>> cf = this.floodFillAnalyzeAsync();
    return cf.thenApply(list -> {
      final World bukkitWorld = requireNonNull(this.spawn.getWorld());
      final int size = list.size();
      final int randomCount = size >> 6;
      final Location[] randomDrops = new Location[randomCount];
      for (int i = 0; i < randomCount; i++) {
        final int index = RandomUtils.generateInt(size);
        final BlockVector3 block = list.get(index);
        final int x = block.x();
        final int y = block.y();
        final int z = block.z();
        randomDrops[i] = new Location(bukkitWorld, x, y, z);
      }
      return randomDrops;
    });
  }

  private CompletableFuture<List<BlockVector3>> floodFillAnalyzeAsync() {
    final WorldEdit worldEdit = WorldEdit.getInstance();
    final World bukkitWorld = requireNonNull(this.spawn.getWorld());
    final com.sk89q.worldedit.world.World world = BukkitAdapter.adapt(bukkitWorld);
    final BlockVector3 start = this.getTrueStartingVector();
    final BlockVector3 corner1 = BukkitAdapter.asBlockVector(this.corners[0]);
    final BlockVector3 corner2 = BukkitAdapter.asBlockVector(this.corners[1]);
    final BlockVector3 min = corner1.getMinimum(corner2);
    final BlockVector3 max = corner1.getMaximum(corner2);
    final Queue<BlockVector3> queue = new ConcurrentLinkedQueue<>();
    final Set<BlockVector3> visited = new HashSet<>();
    final List<BlockVector3> valid = new ArrayList<>();
    queue.add(start);
    visited.add(start);

    final CompletableFuture<List<BlockVector3>> future = new CompletableFuture<>();
    final int count = GameProperties.BLOCKS_PER_TICK;
    final ExecutorService virtual = Executors.newVirtualThreadPerTaskExecutor();
    final BukkitRunnable runnable = new BukkitRunnable() {
      @Override
      public void run() {
        int iterations = 0;
        try (final EditSession session = worldEdit.newEditSession(world)) {
          while (!queue.isEmpty() && ++iterations < count) {
            final BlockVector3 current = requireNonNull(queue.poll());
            final Set<BlockVector3> neighbors = TerrainDropAnalyzer.this.getNeighbors(current);
            for (final BlockVector3 neighbor : neighbors) {
              if (!TerrainDropAnalyzer.this.checkValidNeighbor(neighbor, session, visited, min, max)) {
                continue;
              }
              queue.add(neighbor);
              visited.add(neighbor);
              valid.add(neighbor);
            }
          }
        }
        if (queue.isEmpty()) {
          future.complete(valid);
          this.cancel();
        }
      }
    };
    runnable.runTaskTimer(this.plugin, 1L, 1L);
    return future.thenApply(unused -> {
      ExecutorUtils.shutdownExecutorGracefully(virtual);
      return valid;
    });
  }

  private boolean checkValidNeighbor(
    final BlockVector3 neighbor,
    final EditSession session,
    final Set<BlockVector3> visited,
    final BlockVector3 min,
    final BlockVector3 max
  ) {
    if (visited.contains(neighbor)) {
      return false;
    }

    if (!neighbor.containedWithin(min, max)) {
      return false;
    }

    final BlockState state = session.getBlock(neighbor);
    if (!this.checkValidMaterial(state)) {
      return false;
    }

    final BlockVector3 above = neighbor.add(0, 1, 0);
    final BlockState aboveState = session.getBlock(above);
    return this.checkValidMaterialAbove(aboveState);
  }

  private BlockVector3 getTrueStartingVector() {
    final WorldEdit worldEdit = WorldEdit.getInstance();
    final World bukkitWorld = requireNonNull(this.spawn.getWorld());
    final BlockVector3 spawnVector = BukkitAdapter.asBlockVector(this.spawn);
    final com.sk89q.worldedit.world.World world = BukkitAdapter.adapt(bukkitWorld);
    try (final EditSession session = worldEdit.newEditSession(world)) {
      final BlockState state = session.getBlock(spawnVector);
      final BlockType type = state.getBlockType();
      final BlockMaterial material = type.getMaterial();
      if (material.isAir()) {
        return spawnVector.subtract(0, 1, 0);
      }
    }
    return spawnVector;
  }

  private boolean checkValidMaterialAbove(final BlockState state) {
    final BlockType aboveType = state.getBlockType();
    final BlockMaterial aboveMaterial = aboveType.getMaterial();
    return !aboveMaterial.isSolid() || DOOR_TYPES.contains(aboveType);
  }

  private boolean checkValidMaterial(final BlockState state) {
    final BlockType type = state.getBlockType();
    final BlockMaterial material = type.getMaterial();
    return material.isSolid() && !material.isLiquid();
  }

  private Set<BlockVector3> getNeighbors(final BlockVector3 current) {
    final Set<BlockVector3> neighbors = new HashSet<>();
    for (int dx = -1; dx <= 1; dx++) {
      for (int dy = -1; dy <= 1; dy++) {
        for (int dz = -1; dz <= 1; dz++) {
          if (dx != 0 || dy != 0 || dz != 0) {
            final BlockVector3 neighbor = current.add(dx, dy, dz);
            neighbors.add(neighbor);
          }
        }
      }
    }
    return neighbors;
  }
}
