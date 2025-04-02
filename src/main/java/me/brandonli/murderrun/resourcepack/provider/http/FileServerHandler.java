/*

MIT License

Copyright (c) 2025 Brandon Li

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
package me.brandonli.murderrun.resourcepack.provider.http;

import io.netty.buffer.ByteBuf;
import io.netty.buffer.Unpooled;
import io.netty.channel.ChannelFuture;
import io.netty.channel.ChannelFutureListener;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelInboundHandlerAdapter;
import java.io.IOException;
import java.io.RandomAccessFile;
import java.nio.file.Files;
import java.nio.file.Path;
import me.brandonli.murderrun.utils.IOUtils;

public final class FileServerHandler extends ChannelInboundHandlerAdapter {

  private static final String RESPONSE_HEADERS_TEMPLATE =
    """
    HTTP/1.1 200 OK\r
    Content-Type: application/octet-stream\r
    Content-Length: %s\r
    Content-Disposition: attachment; filename="%s"\r
    Connection: keep-alive\r
    \r
    """;

  private final Path filePath;

  public FileServerHandler(final Path filePath) {
    this.filePath = filePath;
  }

  @Override
  public void channelActive(final ChannelHandlerContext ctx) throws Exception {
    final String path = this.filePath.toString();
    try (final RandomAccessFile file = new RandomAccessFile(path, "r")) {
      final byte[] responseHeaders = this.createHeader(file);
      final byte[] fileContent = Files.readAllBytes(this.filePath);
      final ByteBuf buf = Unpooled.copiedBuffer(responseHeaders);
      ctx.write(buf);
      final ByteBuf copied = Unpooled.copiedBuffer(fileContent);
      final ChannelFuture future = ctx.writeAndFlush(copied);
      future.addListener(ChannelFutureListener.CLOSE);
    }
  }

  public byte[] createHeader(final RandomAccessFile file) throws IOException {
    final long fileLength = file.length();
    final String fileName = IOUtils.getName(this.filePath);
    final String responseHeaders = String.format(RESPONSE_HEADERS_TEMPLATE, fileLength, fileName);
    return responseHeaders.getBytes();
  }

  @Override
  public void exceptionCaught(final ChannelHandlerContext ctx, final Throwable cause) {
    throw new AssertionError(cause);
  }
}
