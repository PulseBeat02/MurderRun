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
package io.github.pulsebeat02.murderrun.resourcepack.provider.netty;

import io.github.pulsebeat02.murderrun.resourcepack.provider.ProviderMethod;
import io.github.pulsebeat02.murderrun.resourcepack.provider.ResourcePackProvider;
import io.github.pulsebeat02.murderrun.resourcepack.provider.netty.injector.ByteBuddyBukkitInjector;
import io.github.pulsebeat02.murderrun.resourcepack.provider.netty.injector.ReflectBukkitInjector;
import io.github.pulsebeat02.murderrun.utils.IOUtils;
import java.io.IOException;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.nio.file.Path;
import org.bukkit.Bukkit;
import org.checkerframework.checker.initialization.qual.UnderInitialization;

public final class NettyHosting extends ResourcePackProvider {

  private static final String IP_URL = "http://checkip.amazonaws.com";

  private final String url;
  private boolean injected;

  public NettyHosting() {
    super(ProviderMethod.ON_SERVER);
    this.url = this.getPackUrl();
  }

  private String getPackUrl(@UnderInitialization NettyHosting this) {
    final String ip = this.getPublicAddress();
    final int port = Bukkit.getPort();
    return "http://%s:%s".formatted(ip, port);
  }

  private String getPublicAddress(@UnderInitialization NettyHosting this) {
    final String ip = Bukkit.getIp();
    if (!ip.isEmpty()) {
      return ip;
    }

    try {
      final URI uri = URI.create(IP_URL);
      final HttpClient client = HttpClient.newHttpClient();
      final HttpRequest request = HttpRequest.newBuilder().uri(uri).build();
      final HttpResponse.BodyHandler<String> handler = HttpResponse.BodyHandlers.ofString();
      final HttpResponse<String> response = client.send(request, handler);
      final String address = response.body();
      final String encodedAddress = address.trim();
      final URI check = URI.create(encodedAddress);
      final boolean valid = IOUtils.checkValidUrl(check);
      return valid ? address : "localhost";
    } catch (final IOException | InterruptedException e) {
      final Thread current = Thread.currentThread();
      current.interrupt();
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
