package io.github.pulsebeat02.murderrun.structure;

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
