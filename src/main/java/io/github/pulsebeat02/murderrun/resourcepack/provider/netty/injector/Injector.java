package io.github.pulsebeat02.murderrun.resourcepack.provider.netty.injector;

import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelDuplexHandler;
import io.netty.channel.ChannelHandler.Sharable;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelPipeline;

@Sharable
public abstract class Injector extends ChannelDuplexHandler {

  public abstract boolean isRelevant(InjectorContext ctx);

  public abstract boolean onRead(ChannelHandlerContext ctx, ByteBuf buf);

  @Override
  public void channelRead(final ChannelHandlerContext ctx, final Object msg) throws Exception {
    final ByteBuf buf = (ByteBuf) msg;
    final ChannelPipeline pipeline = ctx.pipeline();
    final InjectorContext context = new InjectorContext(pipeline, buf);
    if (!this.isRelevant(context)) {
      super.channelRead(ctx, msg);
      return;
    }
    final boolean shouldDelegate = !this.onRead(ctx, buf);
    if (shouldDelegate) {
      super.channelRead(ctx, msg);
    }
  }

  public void register() {}
}
