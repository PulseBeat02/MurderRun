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

import java.util.Set;
import java.util.function.Consumer;
import org.bukkit.plugin.Plugin;
import org.jetbrains.annotations.Unmodifiable;

public interface EventBus {
  <T extends MurderRunEvent> EventSubscription<T> subscribe(Plugin plugin, Class<T> eventType, Consumer<? super T> handler);

  <T extends MurderRunEvent> EventSubscription<T> subscribe(Plugin plugin, Class<T> eventType, Consumer<? super T> handler, int priority);

  <T extends MurderRunEvent> void unsubscribe(EventSubscription<T> subscription);

  void unsubscribe(Plugin plugin);

  <T extends MurderRunEvent> void unsubscribe(Plugin plugin, Class<T> eventType);

  @Unmodifiable
  Set<EventSubscription<?>> getSubscriptions(Plugin plugin);

  <T extends MurderRunEvent> @Unmodifiable Set<EventSubscription<T>> getSubscriptions(Plugin plugin, Class<T> eventType);

  <T extends MurderRunEvent> @Unmodifiable Set<EventSubscription<?>> getAllSubscriptions(Plugin plugin, Class<T> eventType);
}
