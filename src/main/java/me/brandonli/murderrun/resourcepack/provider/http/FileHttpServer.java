/*
 * This file is part of Murder Run, a spin-off game-mode of Dead by Daylight
 * Copyright (C) Brandon Li <https://brandonli.me/>
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program.  If not, see <https://www.gnu.org/licenses/>.
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

public final class FileHttpServer {

  private final int port;
  private final Path filePath;
  private final ExecutorService service;

  private volatile EventLoopGroup bossGroup;
  private volatile EventLoopGroup workerGroup;

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
        this.service);
    try {
      latch.await();
    } catch (final InterruptedException e) {
      final Thread current = Thread.currentThread();
      current.interrupt();
      throw new AssertionError(e);
    }
  }

  private ChannelFuture addServerListener(final ServerBootstrap b, final CountDownLatch latch) {
    final ChannelFuture before = b.bind(this.port);
    final ChannelFutureListener listener = getChannelFutureListener(latch);
    before.addListener(listener);
    return before;
  }

  private ServerBootstrap initializeServerBootstrap() {
    final FileHttpChannelInitializer initializer = new FileHttpChannelInitializer(this);
    final ServerBootstrap b = new ServerBootstrap();
    this.bossGroup = new NioEventLoopGroup();
    this.workerGroup = new NioEventLoopGroup();
    b.group(this.bossGroup, this.workerGroup)
        .channel(NioServerSocketChannel.class)
        .childHandler(initializer);
    return b;
  }

  private static ChannelFutureListener getChannelFutureListener(final CountDownLatch latch) {
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
