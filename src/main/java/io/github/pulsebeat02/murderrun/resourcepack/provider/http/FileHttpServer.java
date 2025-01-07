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
package io.github.pulsebeat02.murderrun.resourcepack.provider.http;

import io.netty.bootstrap.ServerBootstrap;
import io.netty.channel.*;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.SocketChannel;
import io.netty.channel.socket.nio.NioServerSocketChannel;
import io.netty.handler.stream.ChunkedWriteHandler;
import java.nio.file.Path;
import org.jetbrains.annotations.NotNull;

public final class FileHttpServer {

  private final int port;
  private final Path filePath;
  private EventLoopGroup bossGroup;
  private EventLoopGroup workerGroup;

  public FileHttpServer(final int port, final Path filePath) {
    this.port = port;
    this.filePath = filePath;
  }

  public void start() {
    this.bossGroup = new NioEventLoopGroup();
    this.workerGroup = new NioEventLoopGroup();
    try {
      final ServerBootstrap b = new ServerBootstrap();
      b.group(this.bossGroup, this.workerGroup).channel(NioServerSocketChannel.class).childHandler(this.getChannelInitializer());
      final ChannelFuture before = b.bind(this.port);
      final ChannelFuture f = before.sync();
      final Channel channel = f.channel();
      final ChannelFuture closeFuture = channel.closeFuture();
      closeFuture.sync();
    } catch (final InterruptedException e) {
      throw new AssertionError(e);
    } finally {
      this.stop();
    }
  }

  public void stop() {
    if (this.bossGroup != null) {
      this.bossGroup.shutdownGracefully();
    }
    if (this.workerGroup != null) {
      this.workerGroup.shutdownGracefully();
    }
  }

  private @NotNull ChannelInitializer<SocketChannel> getChannelInitializer() {
    return new ChannelInitializer<>() {
      @Override
      public void initChannel(final SocketChannel ch) {
        final ChannelPipeline p = ch.pipeline();
        p.addLast(new ChunkedWriteHandler());
        p.addLast(new FileServerHandler(FileHttpServer.this.filePath));
      }
    };
  }
}
