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
package me.brandonli.murderrun.resourcepack.provider.netty.injector.http;

import io.netty.buffer.ByteBuf;
import io.netty.buffer.ByteBufAllocator;
import io.netty.channel.ChannelHandlerContext;
import java.nio.charset.StandardCharsets;

public final class HttpByteBuf {

  private final ByteBuf inner;

  public HttpByteBuf(final ByteBuf inner) {
    this.inner = inner;
  }

  public ByteBuf getInner() {
    return this.inner;
  }

  public static HttpByteBuf httpBuffer(final ChannelHandlerContext ctx) {
    final ByteBufAllocator allocator = ctx.alloc();
    final ByteBuf buffer = allocator.buffer();
    return new HttpByteBuf(buffer);
  }

  public static HttpByteBuf buildHttpBuffer(final ChannelHandlerContext ctx, final HttpByteBufConsumer block) {
    final HttpByteBuf httpByteBuf = httpBuffer(ctx);
    block.accept(httpByteBuf);
    return httpByteBuf;
  }

  public void writeStatusLine(final String protocolVersion, final int statusCode, final String statusMessage) {
    final String status = "HTTP/%s %d %s%n".formatted(protocolVersion, statusCode, statusMessage);
    this.inner.writeCharSequence(status, StandardCharsets.US_ASCII);
  }

  public void writeHeader(final String header, final String value) {
    final String write = "%s: %s%n".formatted(header, value);
    this.inner.writeCharSequence(write, StandardCharsets.US_ASCII);
  }

  public void writeText(final String text) {
    this.inner.writeCharSequence("\n" + text, StandardCharsets.US_ASCII);
  }

  public void writeBytes(final byte[] bytes) {
    this.inner.writeCharSequence("\n", StandardCharsets.US_ASCII);
    this.inner.writeBytes(bytes);
  }

  @FunctionalInterface
  public interface HttpByteBufConsumer {
    void accept(HttpByteBuf httpByteBuf);
  }
}
