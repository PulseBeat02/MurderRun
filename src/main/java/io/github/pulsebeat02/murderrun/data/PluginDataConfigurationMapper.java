package io.github.pulsebeat02.murderrun.data;

import io.github.pulsebeat02.murderrun.MurderRun;
import java.util.concurrent.CompletableFuture;
import org.bukkit.configuration.file.FileConfiguration;

public final class PluginDataConfigurationMapper {

  private final MurderRun plugin;
  private String hostName;
  private int port;

  public PluginDataConfigurationMapper(final MurderRun plugin) {
    this.plugin = plugin;
    this.hostName = "localhost";
    this.port = 7270;
  }

  public MurderRun getPlugin() {
    return this.plugin;
  }

  public void deserialize() {
    final FileConfiguration config = this.plugin.getConfig();
    this.plugin.saveConfig();
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
}
