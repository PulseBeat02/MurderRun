package io.github.pulsebeat02.murderrun.struct;

import java.util.Iterator;
import java.util.LinkedList;
import org.checkerframework.checker.nullness.qual.NonNull;

public final class CircularBuffer<T> implements Iterable<T> {

  private final LinkedList<T> buffer;
  private final int maxSize;

  public CircularBuffer(final int size) {
    this.maxSize = size;
    this.buffer = new LinkedList<>();
  }

  public void add(final T item) {
    if (this.buffer.size() == this.maxSize) {
      this.buffer.removeFirst();
    }
    this.buffer.addLast(item);
  }

  public T getOldest() {
    return this.buffer.getFirst();
  }

  public boolean isFull() {
    return this.buffer.size() == this.maxSize;
  }

  @Override
  public @NonNull Iterator<T> iterator() {
    return this.buffer.iterator();
  }
}
