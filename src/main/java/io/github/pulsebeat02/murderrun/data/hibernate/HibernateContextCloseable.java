package io.github.pulsebeat02.murderrun.data.hibernate;

import org.checkerframework.checker.nullness.qual.Nullable;

public final class HibernateContextCloseable implements AutoCloseable {

  private final Thread thread;
  private final @Nullable ClassLoader original;

  public HibernateContextCloseable() {
    this.thread = Thread.currentThread();
    this.original = this.thread.getContextClassLoader();
  }

  public void setContextClassLoader() {
    final Class<?> current = this.getClass();
    final ClassLoader loader = current.getClassLoader();
    this.thread.setContextClassLoader(loader);
  }

  @Override
  public void close() {
    this.thread.setContextClassLoader(this.original);
  }
}
