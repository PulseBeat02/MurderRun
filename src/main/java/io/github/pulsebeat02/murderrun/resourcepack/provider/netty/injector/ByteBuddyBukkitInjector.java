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
package io.github.pulsebeat02.murderrun.resourcepack.provider.netty.injector;

import static java.util.Objects.requireNonNull;

import io.github.pulsebeat02.murderrun.resourcepack.provider.netty.injector.http.HttpByteBuf;
import io.github.pulsebeat02.murderrun.resourcepack.provider.netty.injector.http.HttpInjector;
import io.github.pulsebeat02.murderrun.resourcepack.provider.netty.injector.http.HttpRequest;
import io.github.pulsebeat02.murderrun.resourcepack.provider.netty.injector.http.ResourcePackInjector;
import io.netty.channel.Channel;
import io.netty.channel.ChannelPipeline;
import java.nio.file.Path;
import java.util.LinkedHashMap;
import java.util.Map;
import net.bytebuddy.ByteBuddy;
import net.bytebuddy.agent.ByteBuddyAgent;
import net.bytebuddy.asm.Advice;
import net.bytebuddy.description.NamedElement;
import net.bytebuddy.description.type.TypeDescription;
import net.bytebuddy.dynamic.ClassFileLocator;
import net.bytebuddy.dynamic.loading.ClassInjector;
import net.bytebuddy.dynamic.loading.ClassReloadingStrategy;
import net.bytebuddy.implementation.bind.annotation.*;
import net.bytebuddy.matcher.ElementMatcher;
import net.bytebuddy.matcher.ElementMatchers;
import org.checkerframework.checker.initialization.qual.UnderInitialization;

public final class ByteBuddyBukkitInjector {

  private static final String INJECTOR_SYSTEM_PROPERTY = "murderrun.resourcepack";

  private final Path path;
  private final Class<?> clazz;

  public ByteBuddyBukkitInjector(final Path path) {
    this.path = path;
    this.clazz = this.getConnectionClass();
  }

  private Class<?> getConnectionClass(@UnderInitialization ByteBuddyBukkitInjector this) {
    try {
      return Class.forName("net.minecraft.network.NetworkManager");
    } catch (final ClassNotFoundException e) {
      throw new AssertionError(e);
    }
  }

  public void injectAgentIntoServer() {
    try {
      ByteBuddyAgent.install();
      this.setZipProperty();
      this.injectClassesIntoClassLoader();
      this.injectIntoConnectionMethod();
    } catch (final ClassNotFoundException e) {
      throw new AssertionError(e);
    }
  }

  private void injectIntoConnectionMethod() throws ClassNotFoundException {
    final ClassLoader classLoader = requireNonNull(this.clazz.getClassLoader());
    final ElementMatcher.Junction<NamedElement> matcher = ElementMatchers.named("configureSerialization");
    final Advice advice = Advice.to(ConnectionInterceptor.class);
    final ClassReloadingStrategy classLoadingStrategy = ClassReloadingStrategy.fromInstalledAgent();
    new ByteBuddy().rebase(this.clazz).method(matcher).intercept(advice).make().load(classLoader, classLoadingStrategy);
  }

  private void injectClassesIntoClassLoader() throws ClassNotFoundException {
    final ClassLoader classLoader = requireNonNull(this.clazz.getClassLoader());
    new ClassInjector.UsingReflection(classLoader).inject(this.getInjections());
  }

  private void setZipProperty() {
    final Path absolute = this.path.toAbsolutePath();
    final String property = absolute.toString();
    System.setProperty(INJECTOR_SYSTEM_PROPERTY, property); // get around bytebuddy issues with passing args
  }

  private Map<TypeDescription, byte[]> getInjections() {
    final LinkedHashMap<TypeDescription, byte[]> injections = new LinkedHashMap<>();
    this.addInjectionEntry(injections, Injector.class);
    this.addInjectionEntry(injections, InjectorContext.class);
    this.addInjectionEntry(injections, HttpInjector.class);
    this.addInjectionEntry(injections, ResourcePackInjector.class);
    this.addInjectionEntry(injections, HttpRequest.class);
    this.addInjectionEntry(injections, HttpByteBuf.class);
    return injections;
  }

  private void addInjectionEntry(final Map<TypeDescription, byte[]> map, final Class<?> clazz) {
    final TypeDescription typeDescription = new TypeDescription.ForLoadedType(clazz);
    final byte[] classFile = ClassFileLocator.ForClassLoader.read(clazz);
    map.put(typeDescription, classFile);
  }

  public static class ConnectionInterceptor {

    private ConnectionInterceptor() {
      throw new UnsupportedOperationException("Utility class cannot be instantiated");
    }

    @Advice.OnMethodExit
    @RuntimeType
    public static void intercept(@Advice.AllArguments final Object[] allArguments) {
      final ChannelPipeline pipeline = (ChannelPipeline) allArguments[0];
      final Channel channel = pipeline.channel();
      final ChannelPipeline pipeline1 = channel.pipeline();
      final ResourcePackInjector injector = new ResourcePackInjector();
      pipeline1.addFirst(injector);
    }
  }
}
