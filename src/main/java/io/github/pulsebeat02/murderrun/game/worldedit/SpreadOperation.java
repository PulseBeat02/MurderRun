package io.github.pulsebeat02.murderrun.game.worldedit;

import com.sk89q.worldedit.function.operation.Operation;
import com.sk89q.worldedit.function.operation.RunContext;
import com.sk89q.worldedit.math.BlockVector3;
import com.sk89q.worldedit.world.block.BaseBlock;
import io.github.pulsebeat02.murderrun.MurderRun;
import io.github.pulsebeat02.murderrun.game.GameProperties;
import java.util.Iterator;
import java.util.Map;
import java.util.function.BiFunction;
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
  public void cancel() {}
}
