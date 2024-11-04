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
