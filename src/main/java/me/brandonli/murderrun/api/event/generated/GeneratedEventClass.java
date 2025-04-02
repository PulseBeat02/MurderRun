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
package me.brandonli.murderrun.api.event.generated;

import static java.util.Objects.requireNonNull;
import static net.bytebuddy.matcher.ElementMatchers.*;

import java.lang.invoke.MethodHandle;
import java.lang.invoke.MethodHandles;
import java.lang.invoke.MethodType;
import java.lang.reflect.Method;
import java.util.Arrays;
import java.util.Comparator;
import me.brandonli.murderrun.MurderRun;
import me.brandonli.murderrun.api.event.Cancellable;
import me.brandonli.murderrun.api.event.MurderRunEvent;
import net.bytebuddy.ByteBuddy;
import net.bytebuddy.ClassFileVersion;
import net.bytebuddy.description.modifier.Visibility;
import net.bytebuddy.description.type.TypeDescription;
import net.bytebuddy.dynamic.DynamicType;
import net.bytebuddy.dynamic.scaffold.subclass.ConstructorStrategy;
import net.bytebuddy.implementation.FieldAccessor;
import net.bytebuddy.implementation.FixedValue;
import net.bytebuddy.implementation.MethodCall;

@SuppressWarnings("all") // checker
public final class GeneratedEventClass {

  private final MethodHandle constructor;
  private final MethodHandle[] setters;

  public GeneratedEventClass(final Class<? extends MurderRunEvent> eventClass) {
    try {
      final TypeDescription eventType = new TypeDescription.ForLoadedType(eventClass);
      final String implName = this.getImplementationName(eventClass);
      final Method[] methods = this.getInterfaceMethods(eventClass);
      final Class<? extends SimpleMurderRunEvent> implClass = this.createClass(eventClass, implName, eventType, methods);
      this.constructor = this.createHandle(implClass);
      this.setters = new MethodHandle[methods.length];
      this.createSetters(methods, implClass);
    } catch (final Throwable e) {
      throw new AssertionError(e);
    }
  }

  private void createSetters(final Method[] methods, final Class<? extends SimpleMurderRunEvent> implClass) throws Throwable {
    final Object object = this.constructor.invoke((Object) null);
    final SimpleMurderRunEvent event = (SimpleMurderRunEvent) object;
    final MethodHandles.Lookup lookup = event.mhl();
    for (int i = 0; i < methods.length; i++) {
      final Method m = methods[i];
      final String name = m.getName();
      final Class<?> returnType = m.getReturnType();
      final MethodType methodType = MethodType.methodType(Void.TYPE, SimpleMurderRunEvent.class, Object.class);
      this.setters[i] = lookup.findSetter(implClass, name, returnType).asType(methodType);
    }
  }

  private MethodHandle createHandle(final Class<? extends SimpleMurderRunEvent> implClass)
    throws NoSuchMethodException, IllegalAccessException {
    return MethodHandles.publicLookup()
      .in(implClass)
      .findConstructor(implClass, MethodType.methodType(Void.TYPE, MurderRun.class))
      .asType(MethodType.methodType(SimpleMurderRunEvent.class, MurderRun.class));
  }

  private Class<? extends SimpleMurderRunEvent> createClass(
    final Class<? extends MurderRunEvent> eventClass,
    final String implName,
    final TypeDescription eventType,
    final Method[] methods
  ) throws NoSuchMethodException {
    final DynamicType.Builder<SimpleMurderRunEvent> builder = this.constructBuilder(eventClass, implName, eventType, methods);
    final Class<GeneratedEventClass> current = GeneratedEventClass.class;
    final ClassLoader loader = current.getClassLoader();
    final DynamicType.Unloaded<SimpleMurderRunEvent> built = builder.make();
    final DynamicType.Loaded<SimpleMurderRunEvent> loaded = built.load(loader);
    return loaded.getLoaded();
  }

  private DynamicType.Builder<SimpleMurderRunEvent> constructBuilder(
    final Class<? extends MurderRunEvent> eventClass,
    final String implName,
    final TypeDescription eventType,
    final Method[] methods
  ) throws NoSuchMethodException {
    DynamicType.Builder<SimpleMurderRunEvent> builder = this.createBuilder(implName, eventType);
    builder = this.applyMethods(methods, builder);
    builder = this.applyCancellable(eventClass, builder);
    return builder;
  }

  private DynamicType.Builder<SimpleMurderRunEvent> createBuilder(final String implName, final TypeDescription eventType)
    throws NoSuchMethodException {
    return new ByteBuddy(ClassFileVersion.JAVA_V8)
      .subclass(SimpleMurderRunEvent.class, ConstructorStrategy.Default.IMITATE_SUPER_CLASS_OPENING)
      .name(implName)
      .implement(eventType)
      .method(named("getEventType").and(returns(Class.class)).and(takesArguments(0)))
      .intercept(FixedValue.value(eventType))
      .method(named("mhl").and(returns(MethodHandles.Lookup.class)).and(takesArguments(0)))
      .intercept(MethodCall.invoke(MethodHandles.class.getMethod("lookup")))
      .withToString();
  }

  private DynamicType.Builder<SimpleMurderRunEvent> applyCancellable(
    final Class<? extends MurderRunEvent> eventClass,
    DynamicType.Builder<SimpleMurderRunEvent> builder
  ) {
    final boolean cancellable = Cancellable.class.isAssignableFrom(eventClass);
    final FieldAccessor.OwnerTypeLocatable fieldAccessor = FieldAccessor.ofField("cancelled");
    if (cancellable) {
      builder = builder
        .defineField("cancelled", Boolean.TYPE, Visibility.PRIVATE)
        .method(named("isCancelled"))
        .intercept(fieldAccessor)
        .method(named("setCancelled"))
        .intercept(fieldAccessor);
    }
    return builder;
  }

  private DynamicType.Builder<SimpleMurderRunEvent> applyMethods(
    final Method[] methods,
    DynamicType.Builder<SimpleMurderRunEvent> builder
  ) {
    for (final Method method : methods) {
      final String name = method.getName();
      final Class<?> returnType = method.getReturnType();
      final FieldAccessor.OwnerTypeLocatable fieldAccessor = FieldAccessor.ofField(name);
      builder = builder
        .defineField(name, returnType, Visibility.PRIVATE)
        .method(named(name).and(returns(returnType)))
        .intercept(fieldAccessor);
    }
    return builder;
  }

  private Method[] getInterfaceMethods(final Class<? extends MurderRunEvent> eventClass) {
    return Arrays.stream(eventClass.getMethods())
      .filter(m -> m.isAnnotationPresent(Param.class))
      .filter(m -> !m.isAnnotationPresent(NonInvokable.class))
      .sorted(Comparator.comparingInt(m -> m.getAnnotation(Param.class).value()))
      .toArray(Method[]::new);
  }

  private String getImplementationName(final Class<? extends MurderRunEvent> eventClass) {
    final Class<MurderRunEvent> clazz = MurderRunEvent.class;
    final Package pkg = requireNonNull(clazz.getPackage());
    final String murderRunEventPackage = pkg.getName();
    final String eventClassName = eventClass.getName();
    final int packageLength = murderRunEventPackage.length();
    final String eventSuffix = eventClassName.substring(packageLength);
    final String generatedEventClassName = GeneratedEventClass.class.getName();
    final int lastDotIndex = generatedEventClassName.lastIndexOf('.');
    final String basePackage = generatedEventClassName.substring(0, lastDotIndex);
    return "%s.impl%sImpl".formatted(basePackage, eventSuffix);
  }

  public MurderRunEvent newInstance(final MurderRun api, final Object... values) {
    final int length = this.setters.length;
    final int actualLength = values.length;
    if (actualLength != length) {
      final String msg = "Expected %s values but got %s".formatted(length, actualLength);
      throw new AssertionError(msg);
    }
    try {
      final SimpleMurderRunEvent instance = (SimpleMurderRunEvent) this.constructor.invoke(api);
      for (int i = 0; i < this.setters.length; i++) {
        final Object value = values[i];
        this.setters[i].invokeExact(instance, value);
      }
      return instance;
    } catch (final Throwable e) {
      throw new AssertionError(e);
    }
  }
}
