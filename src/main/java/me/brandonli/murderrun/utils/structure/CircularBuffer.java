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
package me.brandonli.murderrun.utils.structure;

import static java.util.Objects.requireNonNull;

import java.util.Collection;
import java.util.Deque;
import java.util.Iterator;
import java.util.LinkedList;
import org.checkerframework.checker.nullness.qual.NonNull;

public final class CircularBuffer<T> implements Iterable<T> {

  private final Deque<T> buffer;
  private final int maxSize;

  public CircularBuffer(final int size) {
    this.maxSize = size;
    this.buffer = new LinkedList<>();
  }

  public void add(final T item) {
    final int currentSize = this.buffer.size();
    if (currentSize == this.maxSize) {
      this.buffer.removeFirst();
    }
    this.buffer.addLast(item);
  }

  public T getOldest() {
    if (this.buffer.isEmpty()) {
      throw new IllegalStateException("Cannot get oldest element from empty buffer");
    }
    return this.buffer.getFirst();
  }

  public void remove(final T item) {
    this.buffer.remove(requireNonNull(item));
  }

  public void removeAll(final Collection<T> items) {
    this.buffer.removeAll(requireNonNull(items));
  }

  @Override
  public @NonNull Iterator<T> iterator() {
    return this.buffer.iterator();
  }
}
