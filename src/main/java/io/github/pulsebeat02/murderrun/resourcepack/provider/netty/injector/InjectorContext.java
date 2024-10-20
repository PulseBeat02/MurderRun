package io.github.pulsebeat02.murderrun.resourcepack.provider.netty.injector;

import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelPipeline;

public final class InjectorContext {

  private final ChannelPipeline pipeline;
  private final ByteBuf message;

  public InjectorContext(final ChannelPipeline pipeline, final ByteBuf message) {
    this.pipeline = pipeline;
    this.message = message;
  }

  public ChannelPipeline getPipeline() {
    return this.pipeline;
  }

  public ByteBuf getMessage() {
    return this.message;
  }
}
