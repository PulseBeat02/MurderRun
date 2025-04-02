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
package me.brandonli.murderrun.resourcepack.provider.netty;

import java.nio.file.Path;
import me.brandonli.murderrun.MurderRun;
import me.brandonli.murderrun.resourcepack.provider.ProviderMethod;
import me.brandonli.murderrun.resourcepack.provider.ResourcePackProvider;
import me.brandonli.murderrun.resourcepack.provider.netty.injector.ByteBuddyBukkitInjector;
import me.brandonli.murderrun.resourcepack.provider.netty.injector.ReflectBukkitInjector;
import me.brandonli.murderrun.utils.IOUtils;
import org.bukkit.Bukkit;
import org.checkerframework.checker.initialization.qual.UnderInitialization;

public final class NettyHosting extends ResourcePackProvider {

  private final String url;

  public NettyHosting(final MurderRun plugin) {
    super(plugin, ProviderMethod.ON_SERVER);
    this.url = this.getPackUrl();
  }

  private String getPackUrl(@UnderInitialization NettyHosting this) {
    final String ip = IOUtils.getPublicIPAddress();
    final int port = Bukkit.getPort();
    return "http://%s:%s".formatted(ip, port);
  }

  @Override
  public String getRawUrl() {
    return this.url;
  }

  @Override
  public void start() {
    super.start();
    final Path zip = ResourcePackProvider.getServerPack();
    this.injectHandler(zip);
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
