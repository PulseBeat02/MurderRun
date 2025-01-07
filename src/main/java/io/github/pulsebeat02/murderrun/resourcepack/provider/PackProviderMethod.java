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
package io.github.pulsebeat02.murderrun.resourcepack.provider;

import io.github.pulsebeat02.murderrun.MurderRun;
import io.github.pulsebeat02.murderrun.data.yaml.PluginDataConfigurationMapper;
import io.github.pulsebeat02.murderrun.resourcepack.provider.http.ServerPackHosting;
import io.github.pulsebeat02.murderrun.resourcepack.provider.netty.NettyHosting;
import org.bukkit.Bukkit;
import org.bukkit.scheduler.BukkitScheduler;

public final class PackProviderMethod {

  private final MurderRun plugin;

  public PackProviderMethod(final MurderRun plugin) {
    this.plugin = plugin;
  }

  public ResourcePackProvider getProvider() {
    final PluginDataConfigurationMapper config = this.plugin.getConfiguration();
    final ProviderMethod method = config.getProviderMethod();
    final ResourcePackProvider provider =
      switch (method) {
        case MC_PACK_HOSTING -> new MCPackHosting();
        case LOCALLY_HOSTED_DAEMON -> {
          final String hostName = config.getHostName();
          final int port = config.getPort();
          yield new ServerPackHosting(hostName, port);
        }
        case ON_SERVER -> new NettyHosting();
      };
    this.deferCaching(provider);
    return provider;
  }

  private void deferCaching(final ResourcePackProvider provider) {
    // wait for netty because url isn't valid yet
    final BukkitScheduler scheduler = Bukkit.getScheduler();
    scheduler.runTaskAsynchronously(this.plugin, provider::cachePack);
  }
}
