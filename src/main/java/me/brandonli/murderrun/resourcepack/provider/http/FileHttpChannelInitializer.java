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

import io.netty.channel.ChannelInitializer;
import io.netty.channel.ChannelPipeline;
import io.netty.channel.socket.SocketChannel;
import io.netty.handler.stream.ChunkedWriteHandler;
import java.nio.file.Path;

public final class FileHttpChannelInitializer extends ChannelInitializer<SocketChannel> {

  private final FileHttpServer server;

  public FileHttpChannelInitializer(final FileHttpServer server) {
    this.server = server;
  }

  @Override
  public void initChannel(final SocketChannel ch) {
    final Path file = this.server.getFilePath();
    final ChannelPipeline p = ch.pipeline();
    final ChunkedWriteHandler handler = new ChunkedWriteHandler();
    final FileServerHandler fileHandler = new FileServerHandler(file);
    p.addLast(handler);
    p.addLast(fileHandler);
  }
}
