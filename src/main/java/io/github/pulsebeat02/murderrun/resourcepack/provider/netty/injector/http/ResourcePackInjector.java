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
