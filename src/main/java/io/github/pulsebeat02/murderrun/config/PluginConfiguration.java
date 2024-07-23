package io.github.pulsebeat02.murderrun.config;

import io.github.pulsebeat02.murderrun.MurderRun;
import org.bukkit.configuration.file.FileConfiguration;

public final class PluginConfiguration {

  private final MurderRun plugin;
  private String hostName;
  private int port;

  public PluginConfiguration(final MurderRun plugin) {
    this.plugin = plugin;
    this.hostName = "127.0.0.1";
    this.port = 7270;
  }

  public void deserialize() {
    this.plugin.saveDefaultConfig();
    final FileConfiguration config = this.plugin.getConfig();
    this.hostName = config.getString("server.host-name");
    this.port = config.getInt("server.port");
  }

  public void serialize() {
    final FileConfiguration config = this.plugin.getConfig();
    config.set("server.host-name", this.hostName);
    config.set("server.port", this.port);
    this.plugin.saveConfig();
  }

  public String getHostName() {
    return this.hostName;
  }

  public int getPort() {
    return this.port;
  }
}
