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

import io.netty.bootstrap.ServerBootstrap;
import io.netty.channel.*;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.nio.NioServerSocketChannel;
import java.nio.file.Path;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import me.brandonli.murderrun.utils.ExecutorUtils;
import org.jetbrains.annotations.NotNull;

public final class FileHttpServer {

  private final int port;
  private final Path filePath;
  private final ExecutorService service;

  private EventLoopGroup bossGroup;
  private EventLoopGroup workerGroup;

  public FileHttpServer(final int port, final Path filePath) {
    this.port = port;
    this.filePath = filePath;
    this.service = Executors.newSingleThreadExecutor();
  }

  public void start() {
    final CountDownLatch latch = new CountDownLatch(1);
    CompletableFuture.runAsync(
      () -> {
        try {
          final ServerBootstrap b = this.initializeServerBootstrap();
          final ChannelFuture before = this.addServerListener(b, latch);
          final ChannelFuture f = before.sync();
          final Channel channel = f.channel();
          final ChannelFuture closeFuture = channel.closeFuture();
          closeFuture.sync();
        } catch (final InterruptedException e) {
          final Thread current = Thread.currentThread();
          current.interrupt();
          throw new AssertionError(e);
        } finally {
          this.stop();
        }
      },
      this.service
    );
    try {
      latch.await();
    } catch (final InterruptedException e) {
      final Thread current = Thread.currentThread();
      current.interrupt();
      throw new AssertionError(e);
    }
  }

  private @NotNull ChannelFuture addServerListener(final ServerBootstrap b, final CountDownLatch latch) {
    final ChannelFuture before = b.bind(this.port);
    final ChannelFutureListener listener = getChannelFutureListener(latch);
    before.addListener(listener);
    return before;
  }

  private @NotNull ServerBootstrap initializeServerBootstrap() {
    final FileHttpChannelInitializer initializer = new FileHttpChannelInitializer(this);
    final ServerBootstrap b = new ServerBootstrap();
    this.bossGroup = new NioEventLoopGroup();
    this.workerGroup = new NioEventLoopGroup();
    b.group(this.bossGroup, this.workerGroup).channel(NioServerSocketChannel.class).childHandler(initializer);
    return b;
  }

  private static @NotNull ChannelFutureListener getChannelFutureListener(final CountDownLatch latch) {
    return future -> {
      if (future.isSuccess()) {
        latch.countDown();
      } else {
        final Throwable cause = future.cause();
        throw new AssertionError(cause);
      }
    };
  }

  public void stop() {
    if (this.bossGroup != null) {
      this.bossGroup.shutdownGracefully();
    }
    if (this.workerGroup != null) {
      this.workerGroup.shutdownGracefully();
    }
    ExecutorUtils.shutdownExecutorGracefully(this.service);
  }

  public int getPort() {
    return this.port;
  }

  public Path getFilePath() {
    return this.filePath;
  }

  public ExecutorService getService() {
    return this.service;
  }

  public EventLoopGroup getBossGroup() {
    return this.bossGroup;
  }

  public void setBossGroup(final EventLoopGroup bossGroup) {
    this.bossGroup = bossGroup;
  }

  public EventLoopGroup getWorkerGroup() {
    return this.workerGroup;
  }

  public void setWorkerGroup(final EventLoopGroup workerGroup) {
    this.workerGroup = workerGroup;
  }
}
