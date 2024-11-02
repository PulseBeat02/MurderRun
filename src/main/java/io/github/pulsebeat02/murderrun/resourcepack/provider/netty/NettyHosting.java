package io.github.pulsebeat02.murderrun.resourcepack.provider.netty;

import io.github.pulsebeat02.murderrun.resourcepack.provider.ProviderMethod;
import io.github.pulsebeat02.murderrun.resourcepack.provider.ResourcePackProvider;
import io.github.pulsebeat02.murderrun.resourcepack.provider.netty.injector.ByteBuddyBukkitInjector;
import io.github.pulsebeat02.murderrun.resourcepack.provider.netty.injector.ReflectBukkitInjector;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.URI;
import java.net.URL;
import java.nio.file.Path;
import org.bukkit.Bukkit;
import org.checkerframework.checker.initialization.qual.UnderInitialization;
import org.checkerframework.checker.nullness.qual.Nullable;

public final class NettyHosting extends ResourcePackProvider {

  private static final String IP_URL = "https://ipv4.icanhazip.com/";

  private final String url;
  private boolean injected;

  public NettyHosting() {
    super(ProviderMethod.ON_SERVER);
    this.url = this.getPackUrl();
  }

  private String getPackUrl(@UnderInitialization NettyHosting this) {
    final String ip = this.getPublicAddress();
    final int port = Bukkit.getPort();
    if (ip != null) {
      return "http://%s:%s".formatted(ip, port);
    }
    return "http://localhost:%s".formatted(port);
  }

  private @Nullable String getPublicAddress(@UnderInitialization NettyHosting this) {
    final String ip = Bukkit.getIp();
    if (!ip.isEmpty()) {
      return ip;
    }

    try {
      final URI uri = URI.create(IP_URL);
      final URL url = uri.toURL();
      try (
        final InputStream stream = url.openStream();
        final InputStreamReader reader = new InputStreamReader(stream);
        final BufferedReader in = new BufferedReader(reader)
      ) {
        return in.readLine();
      }
    } catch (final IOException e) {
      throw new AssertionError(e);
    }
  }

  @Override
  public String getRawUrl(final Path zip) {
    if (!this.injected) {
      this.injectHandler(zip);
      this.injected = true;
    }
    return this.url;
  }

  private void injectHandler(final Path zip) {
    try {
      final ReflectBukkitInjector injector = new ReflectBukkitInjector(zip);
      injector.inject();
    } catch (final AssertionError e) {
      final ByteBuddyBukkitInjector injector = new ByteBuddyBukkitInjector(zip);
      injector.injectAgentIntoServer();
      throw new AssertionError(e);
    }
  }
}
