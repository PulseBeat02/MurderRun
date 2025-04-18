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
