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
package me.brandonli.murderrun.api.event;

import java.util.function.Consumer;
import org.bukkit.plugin.Plugin;

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
  public Consumer<? super T> getHandler() {
    return this.handler;
  }

  @Override
  public Class<T> getEventType() {
    return this.eventType;
  }

  @Override
  public Plugin getPlugin() {
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
