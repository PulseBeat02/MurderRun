package io.github.pulsebeat02.murderrun.resourcepack.server;

import io.github.pulsebeat02.murderrun.resourcepack.ServerResourcepack;
import io.github.pulsebeat02.murderrun.utils.ResourceUtils;
import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.security.NoSuchAlgorithmException;
import java.util.concurrent.Executors;
import org.checkerframework.checker.initialization.qual.UnderInitialization;
import org.checkerframework.checker.nullness.qual.NonNull;
import team.unnamed.creative.BuiltResourcePack;
import team.unnamed.creative.base.Writable;
import team.unnamed.creative.server.ResourcePackServer;

public final class PackHostingDaemon {
  private static final ServerResourcepack PACK = new ServerResourcepack();
  private final String hostName;
  private final int port;
  private final ResourcePackServer server;
  private String url;
  private String hash;

  public PackHostingDaemon(final String hostName, final int port) {
    this.hostName = hostName;
    this.port = port;
    this.server = this.buildServer();
  }

  public ResourcePackServer buildServer(@UnderInitialization PackHostingDaemon this) {
    final Path path = PACK.getPath();
    try (final InputStream stream = Files.newInputStream(path)) {
      final Writable writable = Writable.copyInputStream(stream);
      this.url = "http://" + this.hostName + ":" + this.port;
      this.hash = ResourceUtils.createPackHash(path);
      final BuiltResourcePack pack = BuiltResourcePack.of(writable, this.hash);
      return ResourcePackServer.server()
          .address(this.url, this.port)
          .pack(pack)
          .executor(Executors.newFixedThreadPool(8))
          .build();
    } catch (final IOException | NoSuchAlgorithmException e) {
      throw new AssertionError(e);
    }
  }

  public String getHostName() {
    return this.hostName;
  }

  public int getPort() {
    return this.port;
  }

  public ResourcePackServer getServer() {
    return this.server;
  }

  public void start() {
    this.server.start();
  }

  public void stop() {
    this.server.stop(0);
  }

  public String getUrl() {
    return this.url;
  }

  public void setUrl(final String url) {
    this.url = url;
  }

  public String getHash() {
    return this.hash;
  }

  public void setHash(final String hash) {
    this.hash = hash;
  }
}
