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
package io.github.pulsebeat02.murderrun.api.event;

import static java.lang.invoke.MethodType.methodType;
import static java.util.Objects.requireNonNull;

import com.google.common.collect.ArrayListMultimap;
import com.google.common.collect.ListMultimap;
import io.github.classgraph.ClassGraph;
import io.github.classgraph.ScanResult;
import io.github.pulsebeat02.murderrun.MurderRun;
import io.github.pulsebeat02.murderrun.utils.StreamUtils;
import java.lang.invoke.MethodHandle;
import java.lang.invoke.MethodHandles;
import java.lang.invoke.MethodType;
import java.lang.reflect.Method;
import java.util.*;
import java.util.function.Consumer;
import java.util.stream.Collectors;
import java.util.stream.Stream;
import org.bukkit.plugin.Plugin;
import org.jetbrains.annotations.Unmodifiable;

public final class ApiEventBus implements EventBus {

  private static final MethodHandles.Lookup LOOKUP = MethodHandles.lookup();
  private static final Map<Class<? extends MurderRunEvent>, MethodHandle> KNOWN_EVENT_TYPES;

  static {
    final Set<Class<? extends MurderRunEvent>> classes = scanClasses();
    final Map<Class<? extends MurderRunEvent>, MethodHandle> temp = new HashMap<>();
    for (final Class<? extends MurderRunEvent> event : classes) {
      final MethodHandle handle = getHandle(event);
      temp.put(event, handle);
    }
    KNOWN_EVENT_TYPES = Map.copyOf(temp);
  }

  public static void init() {
    // init events
  }

  private static Set<Class<? extends MurderRunEvent>> scanClasses() {
    final Class<MurderRunEvent> clazz = MurderRunEvent.class;
    final String name = clazz.getName();
    try (final ScanResult scanResult = new ClassGraph().enableClassInfo().scan()) {
      return scanResult
        .getClassesImplementing(name)
        .loadClasses(MurderRunEvent.class)
        .stream()
        .filter(StreamUtils.inverse(Class::isSealed))
        .filter(Class::isInterface)
        .map(clazz1 -> (Class<? extends MurderRunEvent>) clazz1)
        .collect(Collectors.toSet());
    }
  }

  private static <T extends MurderRunEvent> MethodHandle getHandle(final Class<T> eventType) {
    try {
      final Class<? extends T> clazz = EventImplGenerator.generateImplClass(eventType);
      final List<Class<?>> parameterTypes = getParameterTypes(eventType);
      final MethodType type = methodType(void.class, parameterTypes);
      final MethodType modified = type.insertParameterTypes(0, MurderRun.class, Class.class);
      return LOOKUP.findConstructor(clazz, modified);
    } catch (final NoSuchMethodException | IllegalAccessException e) {
      throw new AssertionError(e);
    }
  }

  private static List<Class<?>> getParameterTypes(final Class<?> eventType) {
    final Method[] methods = eventType.getMethods();
    final List<Class<?>> parameterTypes = new ArrayList<>();
    for (final Method method : methods) {
      final String methodName = method.getName();
      final int parameterCount = method.getParameterCount();
      final Class<?> declaringClass = method.getDeclaringClass();
      if (methodName.startsWith("get") && parameterCount == 0 && !declaringClass.equals(MurderRunEvent.class)) {
        parameterTypes.add(method.getReturnType());
      }
    }
    return parameterTypes;
  }

  private final ListMultimap<Class<? extends MurderRunEvent>, EventSubscription<? extends MurderRunEvent>> subscriptions;
  private final MurderRun api;

  public ApiEventBus(final MurderRun api) {
    this.api = api;
    this.subscriptions = ArrayListMultimap.create();
  }

  @Override
  public <T extends MurderRunEvent> EventSubscription<T> subscribe(
    final Plugin plugin,
    final Class<T> eventType,
    final Consumer<? super T> handler
  ) {
    final EventSubscription<T> subscription = new ApiEventSubscription<>(plugin, eventType, handler);
    synchronized (this.subscriptions) {
      final List<EventSubscription<? extends MurderRunEvent>> list = this.subscriptions.get(eventType);
      list.add(subscription);
    }
    return subscription;
  }

  @Override
  public <T extends MurderRunEvent> void unsubscribe(final EventSubscription<T> subscription) {
    synchronized (this.subscriptions) {
      final Class<T> eventType = subscription.getEventType();
      final List<EventSubscription<? extends MurderRunEvent>> list = this.subscriptions.get(eventType);
      list.remove(subscription);
    }
    subscription.unsubscribe();
  }

  @Override
  public void unsubscribe(final Plugin plugin) {
    synchronized (this.subscriptions) {
      final Collection<EventSubscription<? extends MurderRunEvent>> handle = this.subscriptions.values();
      handle.removeIf(subscription -> this.unsubscribePlugin(plugin, subscription));
    }
  }

  @Override
  public <T extends MurderRunEvent> void unsubscribe(final Plugin plugin, final Class<T> eventType) {
    synchronized (this.subscriptions) {
      final List<EventSubscription<? extends MurderRunEvent>> list = this.subscriptions.get(eventType);
      list.removeIf(subscription -> this.unsubscribePlugin(plugin, subscription));
    }
  }

  private boolean unsubscribePlugin(final Plugin plugin, final EventSubscription<? extends MurderRunEvent> subscription) {
    final Plugin subscribedPlugin = subscription.getPlugin();
    if (plugin.equals(subscribedPlugin)) {
      subscription.unsubscribe();
      return true;
    } else {
      return false;
    }
  }

  public void unsubscribeAll() {
    synchronized (this.subscriptions) {
      final Collection<EventSubscription<? extends MurderRunEvent>> handle = this.subscriptions.values();
      handle.forEach(EventSubscription::unsubscribe);
      this.subscriptions.clear();
    }
  }

  @Override
  public @Unmodifiable Set<EventSubscription<? extends MurderRunEvent>> getSubscriptions(final Plugin plugin) {
    synchronized (this.subscriptions) {
      final Collection<EventSubscription<? extends MurderRunEvent>> allSubscriptions = this.subscriptions.values();
      final Stream<EventSubscription<? extends MurderRunEvent>> filteredSubscriptions = allSubscriptions
        .stream()
        .filter(subscription -> plugin.equals(subscription.getPlugin()));
      return filteredSubscriptions.collect(Collectors.toUnmodifiableSet());
    }
  }

  @Override
  @SuppressWarnings("unchecked")
  public <T extends MurderRunEvent> @Unmodifiable Set<EventSubscription<T>> getSubscriptions(
    final Plugin plugin,
    final Class<T> eventType
  ) {
    synchronized (this.subscriptions) {
      final List<EventSubscription<? extends MurderRunEvent>> eventTypeSubscriptions = this.subscriptions.get(eventType);
      final Stream<EventSubscription<? extends MurderRunEvent>> filteredSubscriptions = eventTypeSubscriptions
        .stream()
        .filter(subscription -> plugin.equals(subscription.getPlugin()));
      return filteredSubscriptions.map(subscription -> (EventSubscription<T>) subscription).collect(Collectors.toUnmodifiableSet());
    }
  }

  @Override
  @SuppressWarnings("unchecked")
  public <T extends MurderRunEvent> @Unmodifiable Set<EventSubscription<? extends T>> getAllSubscriptions(
    final Plugin plugin,
    final Class<T> eventType
  ) {
    synchronized (this.subscriptions) {
      final Collection<EventSubscription<? extends MurderRunEvent>> allSubscriptions = this.subscriptions.values();
      final Stream<EventSubscription<? extends MurderRunEvent>> filteredByPlugin = allSubscriptions
        .stream()
        .filter(subscription -> plugin.equals(subscription.getPlugin()));
      final Stream<EventSubscription<? extends MurderRunEvent>> filteredByEventType = filteredByPlugin.filter(subscription ->
        subscription.getEventType().isAssignableFrom(eventType)
      );
      return filteredByEventType.map(subscription -> (EventSubscription<? extends T>) subscription).collect(Collectors.toUnmodifiableSet());
    }
  }

  @SuppressWarnings("unchecked")
  public <T extends MurderRunEvent> T post(final Class<T> type, final Object... args) {
    final T event = this.findEvent(type, args);
    synchronized (this.subscriptions) {
      final Collection<Map.Entry<Class<? extends MurderRunEvent>, EventSubscription<? extends MurderRunEvent>>> entries =
        this.subscriptions.entries();
      final Iterator<Map.Entry<Class<? extends MurderRunEvent>, EventSubscription<? extends MurderRunEvent>>> iterator = entries.iterator();
      while (iterator.hasNext()) {
        final Map.Entry<Class<? extends MurderRunEvent>, EventSubscription<? extends MurderRunEvent>> entry = iterator.next();
        final Class<? extends MurderRunEvent> eventType = entry.getKey();
        final EventSubscription<? extends MurderRunEvent> subscription = entry.getValue();
        if (subscription.isInactive()) {
          iterator.remove();
          continue;
        }
        if (eventType.isInstance(event)) {
          final EventSubscription<T> eventSubscription = (EventSubscription<T>) subscription;
          final Consumer<? super T> handler = eventSubscription.getHandler();
          handler.accept(event);
        }
      }
    }

    return event;
  }

  private <T extends MurderRunEvent> T findEvent(final Class<T> type, final Object[] args) {
    try {
      final MethodHandle handle = requireNonNull(KNOWN_EVENT_TYPES.get(type));
      return type.cast(handle.bindTo(this.api).bindTo(type).invokeWithArguments(args));
    } catch (final Throwable throwable) {
      final String msg = "Failed to create event of unknown type " + type;
      throw new AssertionError(msg, throwable);
    }
  }
}
