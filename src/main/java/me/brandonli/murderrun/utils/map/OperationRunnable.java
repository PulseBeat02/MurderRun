/*

MIT License

Copyright (c) 2025 Brandon Li

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
package me.brandonli.murderrun.utils.map;

import com.sk89q.worldedit.WorldEditException;
import com.sk89q.worldedit.function.operation.Operation;
import com.sk89q.worldedit.function.operation.Operations;
import java.util.Iterator;
import java.util.concurrent.CompletableFuture;
import me.brandonli.murderrun.game.GameProperties;
import org.bukkit.scheduler.BukkitRunnable;

public final class OperationRunnable extends BukkitRunnable {

  private final Iterator<Operation> iterator;
  private final CompletableFuture<Void> future;
  private final int max;

  public OperationRunnable(final Iterator<Operation> iterator, final CompletableFuture<Void> future) {
    this.iterator = iterator;
    this.future = future;
    this.max = GameProperties.WORLDEDIT_MAX_CHUNKS_PER_TICK - 1;
  }

  @Override
  public void run() {
    for (int i = 0; i < this.max; i++) {
      if (!this.iterator.hasNext()) {
        this.future.complete(null);
        this.cancel();
        break;
      }
      try {
        final Operation op = this.iterator.next();
        Operations.complete(op);
      } catch (final WorldEditException e) {
        this.future.completeExceptionally(e);
        this.cancel();
        return;
      }
    }
  }
}
