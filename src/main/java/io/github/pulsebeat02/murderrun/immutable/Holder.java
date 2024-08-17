package io.github.pulsebeat02.murderrun.immutable;

public final class Holder<T> {

  private static final Holder<?> EMPTY = new Holder<>(null);

  private final T value;

  private Holder(final T value) {
    this.value = value;
  }

  public static <T> Holder<T> of(final T value) {
    return new Holder<>(value);
  }

  public static <T> Holder<T> empty() {
    @SuppressWarnings("unchecked")
    final Holder<T> t = (Holder<T>) EMPTY;
    return t;
  }

  public T get() {
    return this.value;
  }

  public boolean isPresent() {
    return this.value != null;
  }

  public boolean isEmpty() {
    return this.value == null;
  }
}
