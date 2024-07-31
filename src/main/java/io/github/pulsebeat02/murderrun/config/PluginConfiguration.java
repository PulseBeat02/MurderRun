package io.github.pulsebeat02.murderrun.config;

import io.github.pulsebeat02.murderrun.MurderRun;
import java.util.concurrent.CompletableFuture;
import org.bukkit.configuration.file.FileConfiguration;
import org.checkerframework.common.value.qual.IntRange;

public final class PluginConfiguration {

  private final MurderRun plugin;
  private String hostName;
  private @IntRange(from = 1, to = 65535) int port;

  public PluginConfiguration(final MurderRun plugin) {
    this.plugin = plugin;
    this.hostName = "localhost";
    this.port = 7270;
  }

  public MurderRun getPlugin() {
    return this.plugin;
  }

  public void deserialize() {
    this.plugin.saveConfig();
    final FileConfiguration config = this.plugin.getConfig();
    this.hostName = this.getHostName(config);
    this.port = this.getPortServerPort(config);
  }

  private int getPortServerPort(final FileConfiguration config) {
    final int value = config.getInt("server.port");
    return value < 1 || value > 65535 ? this.port : value;
  }

  private String getHostName(final FileConfiguration config) {
    final String value = config.getString("server.host-name");
    return value == null ? "localhost" : value;
  }

  public String getHostName() {
    return this.hostName;
  }

  public void setHostName(final String hostName) {
    this.hostName = hostName;
  }

  public void serialize() {
    CompletableFuture.runAsync(() -> {
      final FileConfiguration config = this.plugin.getConfig();
      config.set("server.host-name", this.hostName);
      config.set("server.port", this.port);
      this.plugin.saveConfig();
    });
  }

  public int getPort() {
    return this.port;
  }

  public void setPort(final int port) {
    this.port = port;
  }
}
