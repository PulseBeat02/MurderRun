package io.github.pulsebeat02.murderrun.game.worldedit;

import com.sk89q.worldedit.WorldEditException;
import com.sk89q.worldedit.extent.AbstractBufferingExtent;
import com.sk89q.worldedit.extent.Extent;
import com.sk89q.worldedit.function.operation.Operation;
import com.sk89q.worldedit.math.BlockVector3;
import com.sk89q.worldedit.util.collection.BlockMap;
import com.sk89q.worldedit.world.block.BaseBlock;
import com.sk89q.worldedit.world.block.BlockStateHolder;
import io.github.pulsebeat02.murderrun.MurderRun;
import java.util.Iterator;
import java.util.Map;
import java.util.stream.Stream;
import org.jetbrains.annotations.Nullable;

public final class SchedulingExtent extends AbstractBufferingExtent {

  private final MurderRun plugin;
  private final BlockMap<BaseBlock> buffer;

  public SchedulingExtent(final Extent delegate, final MurderRun plugin) {
    super(delegate);
    this.plugin = plugin;
    this.buffer = BlockMap.createForBaseBlock();
  }

  @Override
  protected @Nullable BaseBlock getBufferedFullBlock(final BlockVector3 position) {
    return this.buffer.computeIfAbsent(position, this.getExtent()::getFullBlock);
  }

  @Override
  public <T extends BlockStateHolder<T>> boolean setBlock(final BlockVector3 position, final T block) {
    this.buffer.remove(position);
    this.buffer.put(position, block.toBaseBlock());
    return true;
  }

  @Override
  protected Operation commitBefore() {
    final Stream<Map.Entry<BlockVector3, BaseBlock>> stream = this.buffer.entrySet().stream();
    final Iterator<Map.Entry<BlockVector3, BaseBlock>> it = stream.iterator();
    return new SpreadOperation(this.plugin, this::setDelegateBlockExceptionally, it);
  }

  private Void setDelegateBlockExceptionally(final BlockVector3 position, final BaseBlock block) {
    try {
      this.setDelegateBlock(position, block);
    } catch (final WorldEditException e) {
      throw new AssertionError(e);
    }
    return null;
  }
}
