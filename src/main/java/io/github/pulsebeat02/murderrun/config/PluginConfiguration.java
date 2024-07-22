package io.github.pulsebeat02.murderrun.config;

import io.github.pulsebeat02.murderrun.MurderRun;

public final class PluginConfiguration {

  private final MurderRun plugin;

  private final int port;

  public PluginConfiguration(final MurderRun plugin) {
    this.plugin = plugin;
    this.port = 7270;
  }

  public MurderRun getPlugin() {
    return this.plugin;
  }

  public int getPort() {
    return this.port;
  }
}
