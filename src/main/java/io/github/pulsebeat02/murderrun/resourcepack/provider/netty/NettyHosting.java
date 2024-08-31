package io.github.pulsebeat02.murderrun.resourcepack.provider.netty;

import io.github.pulsebeat02.murderrun.reflect.PacketToolsProvider;
import io.github.pulsebeat02.murderrun.resourcepack.provider.ProviderMethod;
import io.github.pulsebeat02.murderrun.resourcepack.provider.ResourcePackProvider;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.URL;
import java.nio.file.Path;
import org.bukkit.Bukkit;
import org.checkerframework.checker.nullness.qual.Nullable;

public final class NettyHosting extends ResourcePackProvider {

  private NettyChannelInitializer handler;

  public NettyHosting() {
    super(ProviderMethod.ON_SERVER);
  }

  @Override
  public String getRawUrl(final Path zip) {

    if (this.handler == null) {
      this.handler = new NettyChannelInitializer(zip);
      PacketToolsProvider.PACKET_API.injectNettyHandler("resourcePackInitializer", this.handler);
    }

    final String ip = this.getPublicAddress();
    final int port = Bukkit.getPort();
    if (ip != null) {
      return "http://%s:%s/resourcepack".formatted(ip, port);
    }

    throw new AssertionError("Failed to get public address");
  }

  public @Nullable String getPublicAddress() {
    try {
      final URL url = new URL("http://checkip.amazonaws.com");
      try (final InputStream stream = url.openStream();
          final InputStreamReader reader = new InputStreamReader(stream);
          final BufferedReader in = new BufferedReader(reader)) {
        return in.readLine();
      }
    } catch (final IOException e) {
      throw new AssertionError(e);
    }
  }
}
