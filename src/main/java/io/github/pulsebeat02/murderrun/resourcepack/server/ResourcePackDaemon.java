package io.github.pulsebeat02.murderrun.resourcepack.server;

import io.github.pulsebeat02.murderrun.resourcepack.ServerResourcepack;
import io.github.pulsebeat02.murderrun.utils.ResourceUtils;
import it.unimi.dsi.fastutil.io.FastBufferedInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.security.NoSuchAlgorithmException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import team.unnamed.creative.BuiltResourcePack;
import team.unnamed.creative.base.Writable;
import team.unnamed.creative.server.ResourcePackServer;

public final class ResourcePackDaemon {

  private static final String HOST_URL = "http://%s:%s";

  private final String hostName;
  private final int port;

  private ResourcePackServer server;
  private String url;
  private String hash;

  public ResourcePackDaemon(final String hostName, final int port) {
    this.hostName = hostName;
    this.port = port;
  }

  public void buildPack() {
    final Path path = this.constructResourcepack();
    try (final InputStream stream = Files.newInputStream(path);
        final InputStream fast = new FastBufferedInputStream(stream)) {
      this.url = HOST_URL.formatted(this.hostName, this.port);
      this.hash = ResourceUtils.generateFileHash(path);
      final Writable writable = Writable.copyInputStream(fast);
      final BuiltResourcePack pack = BuiltResourcePack.of(writable, this.hash);
      final ExecutorService service = Executors.newVirtualThreadPerTaskExecutor();
      this.server = ResourcePackServer.server()
          .address(this.hostName, this.port)
          .pack(pack)
          .executor(service)
          .build();
    } catch (final IOException | NoSuchAlgorithmException e) {
      throw new AssertionError(e);
    }
  }

  private Path constructResourcepack() {
    try {
      final ServerResourcepack pack = new ServerResourcepack();
      pack.build();
      return pack.getPath();
    } catch (final IOException e) {
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
