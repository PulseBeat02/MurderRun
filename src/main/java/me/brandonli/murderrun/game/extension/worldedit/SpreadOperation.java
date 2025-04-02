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
