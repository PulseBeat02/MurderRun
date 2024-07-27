package io.github.pulsebeat02.murderrun.config;

import io.github.pulsebeat02.murderrun.MurderRun;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.URI;
import java.net.URL;
import org.bukkit.configuration.file.FileConfiguration;
import org.checkerframework.checker.index.qual.NonNegative;

public final class PluginConfiguration {

  private final MurderRun plugin;
  private String hostName;
  private @NonNegative int port;

  public PluginConfiguration(final MurderRun plugin) {
    this.plugin = plugin;
    this.hostName = "127.0.0.1";
    this.port = 7270;
  }

  public MurderRun getPlugin() {
    return this.plugin;
  }

  public void deserialize() {
    this.plugin.saveDefaultConfig();
    final FileConfiguration config = this.plugin.getConfig();
    this.hostName = this.getHostName(config);
    this.port = this.getPortServerPort(config);
  }

  private int getPortServerPort(final FileConfiguration config) {
    final int value = config.getInt("server.port");
    return value == 0 ? this.port : value;
  }

  private String getHostName(final FileConfiguration config) {
    final String value = config.getString("server.host-name");
    return value == null ? this.getFallBackHostName() : value;
  }

  private String getFallBackHostName() {
    try {
      final URI uri = URI.create("https://checkip.amazonaws.com");
      final URL ip = uri.toURL();
      try (final BufferedReader br = new BufferedReader(new InputStreamReader(ip.openStream()))) {
        final String line = br.readLine();
        return line == null ? this.hostName : line;
      }
    } catch (final IOException e) {
      throw new AssertionError(e);
    }
  }

  public String getHostName() {
    return this.hostName;
  }

  public void setHostName(final String hostName) {
    this.hostName = hostName;
  }

  public void serialize() {
    final FileConfiguration config = this.plugin.getConfig();
    config.set("server.host-name", this.hostName);
    config.set("server.port", this.port);
    this.plugin.saveConfig();
  }

  public int getPort() {
    return this.port;
  }

  public void setPort(final int port) {
    this.port = port;
  }
}
