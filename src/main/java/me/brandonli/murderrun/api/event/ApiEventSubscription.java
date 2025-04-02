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
package me.brandonli.murderrun.api.event;

import java.util.function.Consumer;
import org.bukkit.plugin.Plugin;
import org.jetbrains.annotations.NotNull;

public final class ApiEventSubscription<T extends MurderRunEvent> implements EventSubscription<T> {

  private static final Consumer<?> ILLEGAL_HANDLER = t -> {
    throw new AssertionError("Inactive subscription");
  };

  private final Plugin plugin;
  private final Class<T> eventType;
  private final int priority;

  private Consumer<? super T> handler;
  private boolean active;

  public ApiEventSubscription(final Plugin plugin, final Class<T> eventType, final Consumer<? super T> handler, final int priority) {
    this.plugin = plugin;
    this.eventType = eventType;
    this.handler = handler;
    this.active = true;
    this.priority = priority;
  }

  @Override
  public @NotNull Consumer<? super T> getHandler() {
    return this.handler;
  }

  @Override
  public @NotNull Class<T> getEventType() {
    return this.eventType;
  }

  @Override
  public @NotNull Plugin getPlugin() {
    return this.plugin;
  }

  @Override
  @SuppressWarnings("unchecked")
  public void unsubscribe() {
    this.active = false;
    this.handler = (Consumer<? super T>) ILLEGAL_HANDLER;
  }

  @Override
  public boolean isActive() {
    return this.active;
  }

  @Override
  public int getPriority() {
    return this.priority;
  }
}
