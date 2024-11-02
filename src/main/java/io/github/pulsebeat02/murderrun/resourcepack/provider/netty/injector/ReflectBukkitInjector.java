package io.github.pulsebeat02.murderrun.resourcepack.provider.netty.injector;

import io.github.pulsebeat02.murderrun.reflect.PacketToolAPI;
import io.github.pulsebeat02.murderrun.reflect.PacketToolsProvider;
import io.github.pulsebeat02.murderrun.resourcepack.provider.netty.injector.http.ResourcePackInjector;
import io.netty.channel.*;
import java.nio.file.Path;
import java.util.List;
import java.util.function.Consumer;

public final class ReflectBukkitInjector {

  private static final String INJECTOR_SYSTEM_PROPERTY = "murderrun.resourcepack";

  private final Path path;

  public ReflectBukkitInjector(final Path path) {
    this.path = path;
  }

  public void inject() {
    this.setZipProperty();
    this.install(this::installConsumer);
  }

  private void setZipProperty() {
    final Path absolute = this.path.toAbsolutePath();
    final String property = absolute.toString();
    System.setProperty(INJECTOR_SYSTEM_PROPERTY, property);
  }

  private void installConsumer(final Channel channel) {
    final ChannelPipeline pipeline = channel.pipeline();
    final ResourcePackInjector resourcePackInjector = new ResourcePackInjector();
    pipeline.addFirst(resourcePackInjector);
  }

  private void install(final Consumer<Channel> channelConsumer) {
    final ChannelInboundHandler serverHandler = this.injectServerAdapter(channelConsumer);
    final PacketToolAPI api = PacketToolsProvider.PACKET_API;
    final List<ChannelFuture> channels = api.getServerChannels();
    for (final ChannelFuture channelFuture : channels) {
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
