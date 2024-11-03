package io.github.pulsebeat02.murderrun.resourcepack.provider;

import io.github.pulsebeat02.murderrun.MurderRun;
import io.github.pulsebeat02.murderrun.data.yaml.PluginDataConfigurationMapper;
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
    // for netty because url isn't valid yet
    final BukkitScheduler scheduler = Bukkit.getScheduler();
    scheduler.runTask(this.plugin, provider::cachePack);
  }
}
