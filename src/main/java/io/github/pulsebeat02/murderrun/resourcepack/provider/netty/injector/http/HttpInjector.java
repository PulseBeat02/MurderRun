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
