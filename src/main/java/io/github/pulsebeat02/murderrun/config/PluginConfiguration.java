package io.github.pulsebeat02.murderrun.config;

import io.github.pulsebeat02.murderrun.MurderRun;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.URI;
import java.net.URL;
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

  public MurderRun getPlugin() {
    return this.plugin;
  }

  public void deserialize() {
    this.plugin.saveDefaultConfig();
    final FileConfiguration config = this.plugin.getConfig();
    this.hostName = this.getHostName();
    this.port = config.getInt("server.port");
  }

  private String getHostName(final FileConfiguration config) {
    final String value = config.getString("server.host-name");
    return value == null ? this.getFallBackHostName() : value;
  }

  private String getFallBackHostName() {
    try {
      final URI uri = URI.create("http://checkip.amazonaws.com");
      final URL ip = uri.toURL();
      try (final BufferedReader in = new BufferedReader(new InputStreamReader(ip.openStream()))) {
        final String line = in.readLine();
        return line == null ? "127.0.0.1" : line;
      }
    } catch (final IOException e) {
      throw new AssertionError(e);
    }
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

  public void setHostName(final String hostName) {
    this.hostName = hostName;
  }

  public int getPort() {
    return this.port;
  }

  public void setPort(final int port) {
    this.port = port;
  }
}
