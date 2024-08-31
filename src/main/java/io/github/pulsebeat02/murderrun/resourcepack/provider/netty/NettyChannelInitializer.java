package io.github.pulsebeat02.murderrun.resourcepack.provider.netty;

import io.netty.channel.Channel;
import io.netty.channel.ChannelInitializer;
import io.netty.channel.ChannelPipeline;
import java.nio.file.Path;

public final class NettyChannelInitializer extends ChannelInitializer<Channel> {

  private final Path path;

  public NettyChannelInitializer(final Path path) {
    this.path = path;
  }

  @Override
  protected void initChannel(final Channel channel) throws Exception {
    final ChannelPipeline pipeline = channel.pipeline();
    final ResourcePackHandler handler = new ResourcePackHandler(path);
    pipeline.addFirst("resourcePack", handler);
  }
}
