package io.github.pulsebeat02.murderrun.resourcepack.provider.netty.injector;

import io.github.pulsebeat02.murderrun.resourcepack.provider.netty.injector.http.ResourcePackInjector;
import io.netty.channel.*;
import java.lang.invoke.MethodHandle;
import java.lang.invoke.MethodHandles;
import java.lang.invoke.MethodType;
import java.lang.invoke.VarHandle;
import java.nio.file.Path;
import java.util.List;
import java.util.function.Consumer;
import org.bukkit.Bukkit;
import org.bukkit.Server;

public final class ReflectInjector {

  private static final String INJECTOR_SYSTEM_PROPERTY = "murderrun.resourcepack";

  private static final List<ChannelFuture> CHANNELS;

  static {
    try {
      final Class<?> target = Class.forName("net.minecraft.server.network.ServerConnectionListener");
      final Object connection = getConnectionListener();
      final MethodHandles.Lookup basic = MethodHandles.lookup();
      final MethodHandles.Lookup lookup = MethodHandles.privateLookupIn(target, basic);
      final VarHandle channelsHandle = lookup.findVarHandle(target, "channels", List.class);
      CHANNELS = (List<ChannelFuture>) channelsHandle.get(connection);
    } catch (final Throwable e) {
      throw new RuntimeException(e);
    }
  }

  private static Object getConnectionListener() throws Throwable {
    final MethodHandles.Lookup lookup = MethodHandles.lookup();
    final Object server = getDedicatedServer();
    final Class<?> parent = server.getClass();
    final Class<?> target = Class.forName("net.minecraft.server.network.ServerConnectionListener");
    final MethodType methodType = MethodType.methodType(target);
    final MethodHandle handle = lookup.findVirtual(parent, "getConnection", methodType);
    return handle.invoke(server);
  }

  private static Object getDedicatedServer() throws Throwable {
    final MethodHandles.Lookup lookup = MethodHandles.lookup();
    final Server server = Bukkit.getServer();
    final Class<?> parent = server.getClass();
    final Class<?> target = Class.forName("net.minecraft.server.dedicated.DedicatedServer");
    final MethodType methodType = MethodType.methodType(target);
    final MethodHandle handle = lookup.findVirtual(parent, "getServer", methodType);
    return handle.invoke(server);
  }

  private final Path path;

  public ReflectInjector(final Path path) {
    this.path = path;
  }

  public void inject() {
    this.setZipProperty();
    this.install(this::installConsumer);
  }

  private void setZipProperty() {
    final Path absolute = this.path.toAbsolutePath();
    final String property = absolute.toString();
    System.setProperty(INJECTOR_SYSTEM_PROPERTY, property); // get around bytebuddy issues with passing args
  }

  private void installConsumer(final Channel channel) {
    final var pipeline = channel.pipeline();
    final ResourcePackInjector resourcePackInjector = new ResourcePackInjector();
    pipeline.addFirst(resourcePackInjector);
  }

  private void install(final Consumer<Channel> channelConsumer) {
    final ChannelInboundHandler serverHandler = this.injectServerAdapter(channelConsumer);
    for (final ChannelFuture channelFuture : CHANNELS) {
      final Channel channel = channelFuture.channel();
      final ChannelPipeline pipeline = channel.pipeline();
      pipeline.addFirst(serverHandler);
    }
  }

  private ChannelInboundHandlerAdapter injectServerAdapter(final Consumer<Channel> channelConsumer) {
    final ChannelInitializer<?> beginInitProtocol = this.getBeginInitializer(channelConsumer);
    return new ChannelInboundHandlerAdapter() {
      @Override
      public void channelRead(final ChannelHandlerContext ctx, final Object msg) {
        final Channel channel = (Channel) msg;
        final ChannelPipeline pipeline = channel.pipeline();
        pipeline.addFirst(beginInitProtocol);
        ctx.fireChannelRead(msg);
      }
    };
  }

  private ChannelInitializer<Channel> getBeginInitializer(final Consumer<Channel> channelConsumer) {
    return new ChannelInitializer<>() {
      @Override
      protected void initChannel(final Channel channel) {
        channelConsumer.accept(channel);
      }
    };
  }
}
