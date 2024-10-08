package io.github.pulsebeat02.murderrun.utils;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.function.Predicate;
import java.util.stream.Collector;
import java.util.stream.Collectors;

public final class StreamUtils {

  private static final Collector<?, ?, ?> SHUFFLER = Collectors.collectingAndThen(Collectors.toCollection(ArrayList::new), list -> {
    Collections.shuffle(list);
    return list;
  });

  private StreamUtils() {
    throw new UnsupportedOperationException("Utility class cannot be instantiated");
  }

  public static <T> Predicate<T> inverse(final Predicate<T> predicate) {
    return predicate.negate();
  }

  public static <T, U> Predicate<T> isInstanceOf(final Class<U> clazz) {
    return clazz::isInstance;
  }

  public static <T> Collector<T, ?, List<T>> toShuffledList() {
    return (Collector<T, ?, List<T>>) SHUFFLER;
  }

  public static <T> Collector<T, ?, Set<T>> toSynchronizedSet() {
    return Collectors.toCollection(() -> Collections.synchronizedSet(new HashSet<>()));
  }
}
