package io.github.pulsebeat02.murderrun.resourcepack.provider.netty;

import io.netty.channel.Channel;
import io.netty.channel.ChannelInitializer;
import io.netty.channel.ChannelPipeline;
import io.netty.handler.codec.http.HttpObjectAggregator;
import io.netty.handler.codec.http.HttpServerCodec;
import java.nio.file.Path;

public final class NettyChannelInitializer extends ChannelInitializer<Channel> {

  private static final int MAX_CONTENT_LENGTH = 512 * 1024;

  private final Path path;

  public NettyChannelInitializer(final Path path) {
    this.path = path;
  }

  @Override
  protected void initChannel(final Channel channel) throws Exception {
    final ChannelPipeline pipeline = channel.pipeline();
    pipeline.addFirst("resourcePack", new ResourcePackHandler(this.path));
    pipeline.addFirst("httpAggregator", new HttpObjectAggregator(MAX_CONTENT_LENGTH));
    pipeline.addFirst("httpCodec", new HttpServerCodec());
  }
}
