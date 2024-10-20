package io.github.pulsebeat02.murderrun.resourcepack.provider.netty.injector.http;

import io.github.pulsebeat02.murderrun.resourcepack.provider.netty.injector.Injector;
import io.github.pulsebeat02.murderrun.resourcepack.provider.netty.injector.InjectorContext;
import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelFutureListener;
import io.netty.channel.ChannelHandler.Sharable;
import io.netty.channel.ChannelHandlerContext;

@Sharable
public abstract class HttpInjector extends Injector {

  public abstract HttpByteBuf intercept(final ChannelHandlerContext ctx, final HttpRequest request);

  @Override
  public boolean isRelevant(final InjectorContext ctx) {
    return isRequestGet(ctx.getMessage());
  }

  @Override
  public final boolean onRead(final ChannelHandlerContext ctx, final ByteBuf buf) {
    final HttpRequest request = HttpRequest.parse(buf);
    final HttpByteBuf response = this.intercept(ctx, request);
    final ByteBuf inner = response.getInner();
    ctx.writeAndFlush(inner).addListener(ChannelFutureListener.CLOSE);
    return true;
  }

  private static boolean isRequestMethod(final ByteBuf buf, final String method) {
    for (int i = 0; i < method.length(); i++) {
      final char charAt = method.charAt(i);
      final int byteValue = buf.getUnsignedByte(i);
      if (charAt != (char) byteValue) {
        return false;
      }
    }
    return true;
  }

  private static boolean isRequestGet(final ByteBuf buf) {
    return isRequestMethod(buf, "GET ");
  }
}
