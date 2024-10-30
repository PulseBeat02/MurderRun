package io.github.pulsebeat02.murderrun.resourcepack.provider.netty.injector;

import static java.util.Objects.requireNonNull;

import io.github.pulsebeat02.murderrun.resourcepack.provider.netty.injector.http.HelloWorldInjector;
import io.github.pulsebeat02.murderrun.resourcepack.provider.netty.injector.http.HttpInjector;
import io.netty.channel.Channel;
import io.netty.channel.ChannelPipeline;
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

public final class BukkitInjector {

  public void inject() throws ClassNotFoundException {
    ByteBuddyAgent.install();
    final Class<?> target = Class.forName("net.minecraft.network.Connection");
    final ClassLoader classLoader = requireNonNull(target.getClassLoader());
    final ElementMatcher.Junction<NamedElement> matcher = ElementMatchers.named("configureSerialization");
    final Advice advice = Advice.to(MethodInterceptor.class);
    final ClassReloadingStrategy classLoadingStrategy = ClassReloadingStrategy.fromInstalledAgent();
    new ClassInjector.UsingReflection(classLoader).inject(this.getInjections());
    new ByteBuddy().rebase(target).method(matcher).intercept(advice).make().load(classLoader, classLoadingStrategy);
  }

  private Map<TypeDescription, byte[]> getInjections() {
    return Map.ofEntries(
      this.getInjectionEntry(InjectorContext.class),
      this.getInjectionEntry(Injector.class),
      this.getInjectionEntry(HttpInjector.class),
      this.getInjectionEntry(HelloWorldInjector.class)
    );
  }

  private Map.Entry<TypeDescription, byte[]> getInjectionEntry(final Class<?> clazz) {
    return Map.entry(new TypeDescription.ForLoadedType(clazz), ClassFileLocator.ForClassLoader.read(clazz));
  }

  public static class MethodInterceptor {

    @Advice.OnMethodExit
    @RuntimeType
    public static void intercept(@Advice.AllArguments final Object[] allArguments) throws Exception {
      final ChannelPipeline pipeline = (ChannelPipeline) allArguments[0];
      final Channel channel = pipeline.channel();
      final ChannelPipeline pipeline1 = channel.pipeline();
      final Class<?> injectorClass = HelloWorldInjector.class;
      final HelloWorldInjector injector = (HelloWorldInjector) injectorClass.getDeclaredConstructor().newInstance();
      pipeline1.addFirst(injector);
    }
  }
}
