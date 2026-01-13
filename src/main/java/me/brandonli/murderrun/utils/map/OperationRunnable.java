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

  public OperationRunnable(final GameProperties properties, final Iterator<Operation> iterator, final CompletableFuture<Void> future) {
    this.iterator = iterator;
    this.future = future;
    this.max = properties.getWorldeditMaxChunksPerTick() - 1;
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
