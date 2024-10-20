package io.github.pulsebeat02.murderrun.resourcepack.provider.netty.injector.http;

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
    final String status = "HTTP/%s %d %s\n".formatted(protocolVersion, statusCode, statusMessage);
    this.inner.writeCharSequence(status, StandardCharsets.US_ASCII);
  }

  public void writeHeader(final String header, final String value) {
    final String write = "%s: %s\n".formatted(header, value);
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
