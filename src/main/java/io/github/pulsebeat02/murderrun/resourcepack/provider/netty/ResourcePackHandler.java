package io.github.pulsebeat02.murderrun.resourcepack.provider.netty;

import io.netty.channel.ChannelHandler;
import io.netty.channel.ChannelHandlerAdapter;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.DefaultFileRegion;
import io.netty.handler.codec.http.DefaultHttpResponse;
import io.netty.handler.codec.http.HttpHeaderNames;
import io.netty.handler.codec.http.HttpHeaders;
import io.netty.handler.codec.http.HttpRequest;
import io.netty.handler.codec.http.HttpResponse;
import io.netty.handler.codec.http.HttpResponseStatus;
import io.netty.handler.codec.http.HttpVersion;
import io.netty.handler.codec.http.LastHttpContent;
import java.io.RandomAccessFile;
import java.nio.channels.FileChannel;
import java.nio.file.Path;

@ChannelHandler.Sharable
public final class ResourcePackHandler extends ChannelHandlerAdapter {

  private final Path path;

  public ResourcePackHandler(final Path path) {
    this.path = path;
  }

  @Override
  public void channelRead(final ChannelHandlerContext ctx, final Object msg) throws Exception {

    if (!(msg instanceof final HttpRequest request)) {
      ctx.fireChannelRead(msg);
      return;
    }

    final String path = request.uri();
    if (!path.equals("/resourcepack")) {
      ctx.fireChannelRead(msg);
      return;
    }

    try (final RandomAccessFile file = new RandomAccessFile(this.path.toFile(), "r")) {
      final long length = file.length();
      final FileChannel channel = file.getChannel();
      final DefaultFileRegion region = new DefaultFileRegion(channel, 0, length);
      final HttpResponse response =
          new DefaultHttpResponse(HttpVersion.HTTP_1_1, HttpResponseStatus.OK);
      final HttpHeaders headers = response.headers();
      headers.set(HttpHeaderNames.CONTENT_LENGTH, String.valueOf(length));
      ctx.write(response);
      ctx.write(region);
      ctx.writeAndFlush(LastHttpContent.EMPTY_LAST_CONTENT);
    }
  }
}
