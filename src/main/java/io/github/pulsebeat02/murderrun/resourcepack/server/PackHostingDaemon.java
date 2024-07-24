package io.github.pulsebeat02.murderrun.resourcepack.server;

import io.github.pulsebeat02.murderrun.resourcepack.ServerResourcepack;
import io.github.pulsebeat02.murderrun.utils.ResourceUtils;
import team.unnamed.creative.BuiltResourcePack;
import team.unnamed.creative.base.Writable;
import team.unnamed.creative.server.ResourcePackServer;

import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.security.NoSuchAlgorithmException;
import java.util.concurrent.Executors;

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

  public ResourcePackServer buildServer() {
    final Path path = PACK.getPath();
    try (final InputStream stream = Files.newInputStream(path)) {
      final Writable writable = Writable.copyInputStream(stream);
      this.url = "http://" + this.hostName + ":" + this.port;
      this.hash = ResourceUtils.createPackHash(path);
      final BuiltResourcePack pack = BuiltResourcePack.of(writable, this.hash);
      return ResourcePackServer.server()
          .address(this.hostName, this.port)
          .pack(pack)
          .executor(Executors.newFixedThreadPool(8))
          .build();
    } catch (final IOException | NoSuchAlgorithmException e) {
      throw new AssertionError(e);
    }
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

  public String getHash() {
    return this.hash;
  }
}
