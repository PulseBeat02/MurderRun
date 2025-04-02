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

  private static Set<Class<? extends MurderRunEvent>> scanClasses() {
    final Class<MurderRunEvent> clazz = MurderRunEvent.class;
    final String name = clazz.getName();
    try (final ScanResult scanResult = new ClassGraph().enableClassInfo().scan()) {
      return scanResult
        .getClassesImplementing(name)
        .loadClasses(MurderRunEvent.class)
        .stream()
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

  private final ListMultimap<Class<? extends MurderRunEvent>, EventSubscription<?>> subscriptions;
  private final MurderRun api;

  public ApiEventBus(final MurderRun api) {
    this.api = api;
    this.subscriptions = ArrayListMultimap.create();
  }

  @Override
  public <T extends MurderRunEvent> EventSubscription<T> subscribe(
    final Plugin plugin,
    final Class<T> eventType,
    final Consumer<? super T> handler,
    final int priority
  ) {
    final EventSubscription<T> subscription = new ApiEventSubscription<>(plugin, eventType, handler, priority);
    synchronized (this.subscriptions) {
      final List<EventSubscription<?>> concreteList = this.subscriptions.get(eventType);
      concreteList.add(subscription);
      concreteList.sort(Comparator.comparingInt(EventSubscription::getPriority));
      final Set<Class<? extends MurderRunEvent>> parentEventTypes = this.findParentEventTypes(eventType);
      parentEventTypes.remove(eventType);
      for (final Class<? extends MurderRunEvent> parentType : parentEventTypes) {
        final List<EventSubscription<?>> parentList = this.subscriptions.get(parentType);
        parentList.add(subscription);
        parentList.sort(Comparator.comparingInt(EventSubscription::getPriority));
      }
    }
    return subscription;
  }

  @Override
  public <T extends MurderRunEvent> EventSubscription<T> subscribe(
    final Plugin plugin,
    final Class<T> eventType,
    final Consumer<? super T> handler
  ) {
    return this.subscribe(plugin, eventType, handler, 0);
  }

  @Override
  public <T extends MurderRunEvent> void unsubscribe(final EventSubscription<T> subscription) {
    synchronized (this.subscriptions) {
      final Class<T> eventType = subscription.getEventType();
      final List<EventSubscription<?>> list = this.subscriptions.get(eventType);
      list.remove(subscription);
    }
    subscription.unsubscribe();
  }

  @Override
  public void unsubscribe(final Plugin plugin) {
    synchronized (this.subscriptions) {
      final Collection<EventSubscription<?>> handle = this.subscriptions.values();
      handle.removeIf(subscription -> this.unsubscribePlugin(plugin, subscription));
    }
  }

  @Override
  public <T extends MurderRunEvent> void unsubscribe(final Plugin plugin, final Class<T> eventType) {
    synchronized (this.subscriptions) {
      final List<EventSubscription<?>> list = this.subscriptions.get(eventType);
      list.removeIf(subscription -> this.unsubscribePlugin(plugin, subscription));
    }
  }

  private boolean unsubscribePlugin(final Plugin plugin, final EventSubscription<?> subscription) {
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
      final Collection<EventSubscription<?>> handle = this.subscriptions.values();
      handle.forEach(EventSubscription::unsubscribe);
      this.subscriptions.clear();
    }
  }

  @Override
  public @Unmodifiable Set<EventSubscription<?>> getSubscriptions(final Plugin plugin) {
    synchronized (this.subscriptions) {
      final Collection<EventSubscription<?>> allSubscriptions = this.subscriptions.values();
      final Stream<EventSubscription<?>> filteredSubscriptions = allSubscriptions
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
      final List<EventSubscription<?>> eventTypeSubscriptions = this.subscriptions.get(eventType);
      final Stream<EventSubscription<?>> filteredSubscriptions = eventTypeSubscriptions
        .stream()
        .filter(subscription -> plugin.equals(subscription.getPlugin()));
      return filteredSubscriptions.map(subscription -> (EventSubscription<T>) subscription).collect(Collectors.toUnmodifiableSet());
    }
  }

  @Override
  public @Unmodifiable <T extends MurderRunEvent> Set<EventSubscription<?>> getAllSubscriptions(
    final Plugin plugin,
    final Class<T> eventType
  ) {
    synchronized (this.subscriptions) {
      final Collection<EventSubscription<?>> allSubscriptions = this.subscriptions.values();
      final Stream<EventSubscription<?>> filteredByPlugin = allSubscriptions
        .stream()
        .filter(subscription -> plugin.equals(subscription.getPlugin()));
      final Stream<EventSubscription<?>> filteredByEventType = filteredByPlugin.filter(subscription ->
        subscription.getEventType().isAssignableFrom(eventType)
      );
      return filteredByEventType.map(subscription -> (EventSubscription<?>) subscription).collect(Collectors.toUnmodifiableSet());
    }
  }

  @SuppressWarnings("unchecked")
  public <T extends MurderRunEvent> boolean post(final Class<T> type, final Object... args) {
    if (type.isAnnotationPresent(NonInvokable.class)) {
      final String name = type.getName();
      final String msg = "Event %s is marked as @NonInvokable and cannot be posted".formatted(name);
      throw new AssertionError(msg);
    }
    final T event = this.findEvent(type, args);
    synchronized (this.subscriptions) {
      final Collection<Map.Entry<Class<? extends MurderRunEvent>, EventSubscription<?>>> entries = this.subscriptions.entries();
      final List<EventSubscription<?>> sorted = entries
        .stream()
        .filter(entry -> entry.getKey().isInstance(event))
        .map(Map.Entry::getValue)
        .filter(EventSubscription::isActive)
        .sorted(Comparator.comparingInt(EventSubscription::getPriority))
        .collect(Collectors.toList());
      for (final EventSubscription<?> raw : sorted) {
        final EventSubscription<? super T> sub = (EventSubscription<? super T>) raw;
        final Consumer<? super T> eventHandler = sub.getHandler();
        eventHandler.accept(event);
        if (event instanceof final Cancellable cancellable && cancellable.isCancelled()) {
          return true;
        }
      }
    }
    return false;
  }

  @SuppressWarnings("all")
  private Set<Class<? extends MurderRunEvent>> findParentEventTypes(final Class<? extends MurderRunEvent> eventType) {
    final Set<Class<? extends MurderRunEvent>> parentTypes = new HashSet<>();
    final Queue<Class<?>> processingQueue = new LinkedList<>();
    final Class<?>[] directInterfaces = eventType.getInterfaces();
    Collections.addAll(processingQueue, directInterfaces);
    while (!processingQueue.isEmpty()) {
      final Class<?> currentInterface = processingQueue.poll();
      final boolean isSubtype = MurderRunEvent.class.isAssignableFrom(currentInterface);
      if (isSubtype) {
        final Class<? extends MurderRunEvent> castedInterface = (Class<? extends MurderRunEvent>) currentInterface;
        final boolean alreadyAdded = parentTypes.contains(castedInterface);
        if (!alreadyAdded) {
          parentTypes.add(castedInterface);
          final Class<?>[] superInterfaces = currentInterface.getInterfaces();
          Collections.addAll(processingQueue, superInterfaces);
        }
      }
    }
    return parentTypes;
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
