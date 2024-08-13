package io.github.pulsebeat02.murderrun.data;

import io.github.pulsebeat02.murderrun.MurderRun;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantReadWriteLock;
import org.bukkit.configuration.file.FileConfiguration;

public final class PluginDataConfigurationMapper {

  private static final String SERVER_PORT_FIELD = "server.port";
  private static final String SERVER_HOST_FIELD = "server.host-name";
  private static final String FALL_BACK_HOST_NAME = "localhost";
  private static final int FALL_BACK_PORT = 7270;

  private final ExecutorService service;
  private final MurderRun plugin;
  private final Lock readLock;
  private final Lock writeLock;

  private String hostName;
  private int port;

  public PluginDataConfigurationMapper(final MurderRun plugin) {
    final ReentrantReadWriteLock lock = new ReentrantReadWriteLock();
    this.service = Executors.newVirtualThreadPerTaskExecutor();
    this.plugin = plugin;
    this.hostName = FALL_BACK_HOST_NAME;
    this.port = FALL_BACK_PORT;
    this.readLock = lock.readLock();
    this.writeLock = lock.writeLock();
  }

  public synchronized void shutdown() {
    this.service.shutdown();
  }

  public MurderRun getPlugin() {
    return this.plugin;
  }

  public synchronized void deserialize() {
    this.readLock.lock();
    final FileConfiguration config = this.plugin.getConfig();
    this.plugin.saveConfig();
    this.hostName = this.getHostName(config);
    this.port = this.getPortServerPort(config);
    this.readLock.unlock();
  }

  private int getPortServerPort(final FileConfiguration config) {
    final int value = config.getInt(SERVER_PORT_FIELD);
    return value < 1 || value > 65535 ? this.port : value;
  }

  private String getHostName(final FileConfiguration config) {
    final String value = config.getString(SERVER_HOST_FIELD);
    return value == null ? FALL_BACK_HOST_NAME : value;
  }

  public synchronized String getHostName() {
    return this.hostName;
  }

  public synchronized void serialize() {
    this.writeLock.lock();
    CompletableFuture.runAsync(
        () -> {
          final FileConfiguration config = this.plugin.getConfig();
          config.set(SERVER_HOST_FIELD, this.hostName);
          config.set(SERVER_PORT_FIELD, this.port);
          this.plugin.saveConfig();
        },
        this.service);
    this.writeLock.unlock();
  }

  public synchronized int getPort() {
    return this.port;
  }
}
