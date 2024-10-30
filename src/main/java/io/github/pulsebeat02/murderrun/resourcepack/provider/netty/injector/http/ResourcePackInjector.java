package io.github.pulsebeat02.murderrun.resourcepack.provider.netty.injector.http;

import static java.util.Objects.requireNonNull;

import io.netty.channel.ChannelHandlerContext;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;

public final class ResourcePackInjector extends HttpInjector {

  private static final String INJECTOR_SYSTEM_PROPERTY = "murderrun.resourcepack";

  @Override
  public HttpByteBuf intercept(final ChannelHandlerContext ctx, final HttpRequest request) {
    try {
      final HttpByteBuf buf = HttpByteBuf.httpBuffer(ctx);
      final Path zip = this.getZipPath();
      final byte[] bytes = Files.readAllBytes(zip);
      buf.writeStatusLine("1.1", 200, "OK");
      buf.writeHeader("Content-Type", "application/zip");
      buf.writeBytes(bytes);
      return buf;
    } catch (final IOException e) {
      throw new AssertionError(e);
    }
  }

  private Path getZipPath() {
    final String property = requireNonNull(System.getProperty(INJECTOR_SYSTEM_PROPERTY));
    return Path.of(property);
  }
}
