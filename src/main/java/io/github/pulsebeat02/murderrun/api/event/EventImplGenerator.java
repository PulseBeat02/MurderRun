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

import static java.util.Objects.requireNonNull;

import io.github.pulsebeat02.murderrun.MurderRun;
import java.lang.reflect.Constructor;
import java.lang.reflect.Method;
import java.lang.reflect.Modifier;
import java.util.Collection;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Set;
import net.bytebuddy.ByteBuddy;
import net.bytebuddy.description.NamedElement;
import net.bytebuddy.dynamic.DynamicType;
import net.bytebuddy.dynamic.loading.ClassLoadingStrategy;
import net.bytebuddy.implementation.FieldAccessor;
import net.bytebuddy.implementation.Implementation;
import net.bytebuddy.implementation.MethodCall;
import net.bytebuddy.matcher.ElementMatcher;
import net.bytebuddy.matcher.ElementMatchers;
import org.checkerframework.checker.nullness.qual.KeyFor;

public class EventImplGenerator {

  private static Map<String, Class<?>> extractProperties(final Class<?> eventInterface) {
    final Map<String, Class<?>> properties = new LinkedHashMap<>();
    final Method[] methods = eventInterface.getMethods();
    for (final Method method : methods) {
      final Class<?> declaringClass = method.getDeclaringClass();
      if (declaringClass.equals(MurderRunEvent.class) || declaringClass.equals(Object.class)) {
        continue;
      }
      final int count = method.getParameterCount();
      final Class<?> returnType = method.getReturnType();
      if (count == 0 && returnType != Void.TYPE) {
        final String name = method.getName();
        final Class<?> type = method.getReturnType();
        properties.put(name, type);
      }
    }
    return properties;
  }

  @SuppressWarnings("all") // checker
  private static Class<?>[] buildConstructorParameterTypes(final Map<String, Class<?>> properties) {
    final int propertyCount = properties.size();
    final Class<?>[] ctorParamTypes = new Class<?>[propertyCount + 2];
    ctorParamTypes[0] = MurderRun.class;
    ctorParamTypes[1] = Class.class;

    int i = 0;
    final Collection<Class<?>> values = properties.values();
    for (final Class<?> type : values) {
      ctorParamTypes[i + 2] = type;
      i++;
    }

    return ctorParamTypes;
  }

  @SuppressWarnings("all") // checker
  private static DynamicType.Builder<?> defineFields(final DynamicType.Builder<?> builder, final Map<String, Class<?>> properties) {
    DynamicType.Builder<?> updatedBuilder = builder;
    final Set<Map.Entry<@KeyFor("properties") String, Class<? extends Object>>> entries = properties.entrySet();
    for (final Map.Entry<String, Class<?>> entry : entries) {
      final String fieldName = entry.getKey();
      final Class<?> fieldType = entry.getValue();
      final int modifiers = Modifier.PRIVATE | Modifier.FINAL;
      updatedBuilder = updatedBuilder.defineField(fieldName, fieldType, modifiers);
    }
    return updatedBuilder;
  }

  private static Implementation.Composable defineConstructorImplementation(
    final Constructor<?> superCtor,
    final Map<String, Class<?>> properties
  ) {
    Implementation.Composable ctorImplementation = MethodCall.invoke(superCtor).withArgument(0).withArgument(1);
    int ctorArgIndex = 2;
    final Set<String> keys = properties.keySet();
    for (final String fieldName : keys) {
      final FieldAccessor.OwnerTypeLocatable accessor = FieldAccessor.ofField(fieldName);
      final Implementation.Composable composable = accessor.setsArgumentAt(ctorArgIndex);
      ctorImplementation = ctorImplementation.andThen(composable);
      ctorArgIndex++;
    }
    return ctorImplementation;
  }

  private static DynamicType.Builder<?> defineGetters(final DynamicType.Builder<?> builder, final Map<String, Class<?>> properties) {
    DynamicType.Builder<?> updatedBuilder = builder;
    final Set<String> keys = properties.keySet();
    for (final String methodName : keys) {
      final ElementMatcher<? super NamedElement> matcher = ElementMatchers.named(methodName);
      final FieldAccessor.OwnerTypeLocatable accessor = FieldAccessor.ofField(methodName);
      updatedBuilder = updatedBuilder.method(matcher).intercept(accessor);
    }
    return updatedBuilder;
  }

  @SuppressWarnings("unchecked")
  public static <T extends MurderRunEvent> Class<? extends T> generateImplClass(final Class<T> eventInterface) {
    final Map<String, Class<?>> properties = extractProperties(eventInterface);
    final Class<?>[] ctorParamTypes = buildConstructorParameterTypes(properties);
    try {
      final DynamicType.Builder<?> builder = decorateBuilder(
        properties,
        createByteBuddyBuilder(eventInterface, properties),
        ctorParamTypes
      );
      final ClassLoader loader = eventInterface.getClassLoader();
      final ClassLoadingStrategy.Default strategy = ClassLoadingStrategy.Default.INJECTION;
      final DynamicType.Unloaded<?> unloaded = builder.make();
      final DynamicType.Loaded<?> loaded = unloaded.load(loader, strategy);
      return (Class<? extends T>) loaded.getLoaded();
    } catch (final NoSuchMethodException e) {
      throw new AssertionError("Failed to locate the required super constructor", e);
    }
  }

  public static <T extends MurderRunEvent> T generateImplInstance(final Class<T> eventInterface, final Object... args) {
    try {
      final Class<? extends T> implClass = generateImplClass(eventInterface);
      final Class<?>[] parameters = getParameterTypes(eventInterface);
      final Constructor<? extends T> ctor = implClass.getConstructor(parameters);
      return ctor.newInstance(args);
    } catch (final Exception e) {
      throw new AssertionError("Failed to create an instance of the generated class", e);
    }
  }

  private static Class<?>[] getParameterTypes(final Object... args) {
    final int size = args.length;
    if (size < 2) {
      throw new AssertionError("The number of parameters must be at least 2");
    }
    final Class<?>[] paramTypes = new Class<?>[size];
    for (int i = 0; i < size; i++) {
      paramTypes[i] = args[i].getClass();
    }
    if (paramTypes[0] != MurderRun.class) {
      throw new AssertionError("The first parameter must be of type MurderRun");
    }
    if (paramTypes[1] != Class.class) {
      throw new AssertionError("The second parameter must be of type Class");
    }
    return paramTypes;
  }

  private static DynamicType.Builder<?> decorateBuilder(
    final Map<String, Class<?>> properties,
    DynamicType.Builder<?> builder,
    final Class<?>[] ctorParamTypes
  ) throws NoSuchMethodException {
    final Constructor<?> superCtor = SimpleMurderRunEvent.class.getConstructor(MurderRun.class, Class.class);
    final Implementation.Composable ctorImplementation = defineConstructorImplementation(superCtor, properties);
    builder = builder.defineConstructor(Modifier.PUBLIC).withParameters(ctorParamTypes).intercept(ctorImplementation);
    builder = defineGetters(builder, properties);
    return builder;
  }

  private static <T extends MurderRunEvent> DynamicType.Builder<?> createByteBuddyBuilder(
    final Class<T> eventInterface,
    final Map<String, Class<?>> properties
  ) {
    final Package pkg = requireNonNull(eventInterface.getPackage());
    final String name = pkg.getName();
    final String simpleName = eventInterface.getSimpleName();
    final String implClassName = "%s.impl.%sImpl".formatted(name, simpleName);
    final ByteBuddy byteBuddy = new ByteBuddy();
    DynamicType.Builder<?> builder = byteBuddy.subclass(SimpleMurderRunEvent.class).name(implClassName).implement(eventInterface);
    builder = defineFields(builder, properties);
    return builder;
  }
}
