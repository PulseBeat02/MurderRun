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
package me.brandonli.murderrun.game.extension.worldedit;

import com.sk89q.worldedit.function.operation.Operation;
import com.sk89q.worldedit.function.operation.RunContext;
import com.sk89q.worldedit.math.BlockVector3;
import com.sk89q.worldedit.world.block.BaseBlock;
import java.util.Iterator;
import java.util.Map;
import java.util.function.BiFunction;
import me.brandonli.murderrun.MurderRun;
import me.brandonli.murderrun.game.GameProperties;
import org.bukkit.Bukkit;
import org.bukkit.scheduler.BukkitScheduler;
import org.bukkit.scheduler.BukkitTask;

public final class SpreadOperation implements Operation {

  private final MurderRun plugin;
  private final Iterator<Map.Entry<BlockVector3, BaseBlock>> it;
  private final BiFunction<BlockVector3, BaseBlock, Void> placeFunction;
  private final int blocksPerTick;
  private BukkitTask task;

  public SpreadOperation(
    final MurderRun plugin,
    final BiFunction<BlockVector3, BaseBlock, Void> placeFunction,
    final Iterator<Map.Entry<BlockVector3, BaseBlock>> it
  ) {
    this.blocksPerTick = GameProperties.BLOCKS_PER_TICK;
    this.plugin = plugin;
    this.placeFunction = placeFunction;
    this.it = it;
  }

  @Override
  @SuppressWarnings("all") // checker
  public Operation resume(final RunContext run) {
    if (this.task == null && this.it.hasNext()) {
      final BukkitScheduler scheduler = Bukkit.getScheduler();
      this.task = scheduler.runTaskTimer(this.plugin, this::placeBlock, 1L, 1L);
    }
    return null;
  }

  private void placeBlock() {
    long i = 0;
    while (this.it.hasNext() && i < this.blocksPerTick) {
      final Map.Entry<BlockVector3, BaseBlock> entry = this.it.next();
      final BlockVector3 position = entry.getKey();
      final BaseBlock block = entry.getValue();
      this.placeFunction.apply(position, block);
      i++;
    }
  }

  @Override
  public void cancel() {
    // Do nothing
  }
}
