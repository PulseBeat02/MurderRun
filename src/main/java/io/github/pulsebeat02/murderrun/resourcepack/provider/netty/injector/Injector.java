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
