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

import com.sk89q.worldedit.WorldEditException;
import com.sk89q.worldedit.extent.AbstractBufferingExtent;
import com.sk89q.worldedit.extent.Extent;
import com.sk89q.worldedit.function.operation.Operation;
import com.sk89q.worldedit.math.BlockVector3;
import com.sk89q.worldedit.util.collection.BlockMap;
import com.sk89q.worldedit.world.block.BaseBlock;
import com.sk89q.worldedit.world.block.BlockStateHolder;
import java.util.Iterator;
import java.util.Map;
import java.util.stream.Stream;
import me.brandonli.murderrun.MurderRun;
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
