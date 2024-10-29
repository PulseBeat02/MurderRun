package io.github.pulsebeat02.murderrun.resourcepack.provider.netty.injector.http;

import io.netty.buffer.ByteBuf;
import io.netty.buffer.Unpooled;
import io.netty.channel.ChannelHandlerContext;

public final class HelloWorldInjector extends HttpInjector {

  @Override
  public HttpByteBuf intercept(final ChannelHandlerContext ctx, final HttpRequest request) {
    final ByteBuf buffer = Unpooled.buffer();
    buffer.writeBytes("Hello World".getBytes());
    return new HttpByteBuf(buffer);
  }
}
