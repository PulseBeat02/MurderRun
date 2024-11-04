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
