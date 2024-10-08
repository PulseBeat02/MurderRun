package io.github.pulsebeat02.murderrun.resourcepack.provider;

import io.github.pulsebeat02.murderrun.MurderRun;
import io.github.pulsebeat02.murderrun.data.yaml.PluginDataConfigurationMapper;

public final class PackProviderMethod {

  private final MurderRun plugin;

  public PackProviderMethod(final MurderRun plugin) {
    this.plugin = plugin;
  }

  public ResourcePackProvider getProvider() {
    final PluginDataConfigurationMapper config = this.plugin.getConfiguration();
    final ProviderMethod method = config.getProviderMethod();
    switch (method) {
      case MC_PACK_HOSTING -> {
        return new MCPackHosting();
      }
      case LOCALLY_HOSTED_DAEMON -> {
        final String hostName = config.getHostName();
        final int port = config.getPort();
        return new ServerPackHosting(hostName, port);
      }
      case ON_SERVER -> throw new UnsupportedOperationException("NettyHosting is not implemented yet"); // return new NettyHosting();
      default -> throw new IllegalStateException("Unexpected value: %s".formatted(method));
    }
  }
}
