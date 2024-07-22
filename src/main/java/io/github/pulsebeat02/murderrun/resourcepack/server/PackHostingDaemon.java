package io.github.pulsebeat02.murderrun.resourcepack.server;

import io.github.pulsebeat02.murderrun.MurderRun;
import io.github.pulsebeat02.murderrun.resourcepack.ServerResourcepack;
import io.github.pulsebeat02.murderrun.utils.ResourceUtils;
import team.unnamed.creative.BuiltResourcePack;
import team.unnamed.creative.base.Writable;
import team.unnamed.creative.server.ResourcePackServer;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.security.NoSuchAlgorithmException;
import java.util.concurrent.Executors;

public final class HTTPServer {

  private static final ServerResourcepack PACK;

  static {
    PACK = new ServerResourcepack();
  }

  private final int port;
  private final ResourcePackServer server;

  public HTTPServer(final int port) throws IOException, NoSuchAlgorithmException {
    this.port = port;
    this.server = this.buildServer();
  }

  public ResourcePackServer buildServer() throws IOException, NoSuchAlgorithmException {
    final Path path = PACK.getPath();
    try (final InputStream stream = Files.newInputStream(path)) {
      final Writable writable = Writable.copyInputStream(stream);
      final String hash = ResourceUtils.createPackHash(path);
      final BuiltResourcePack pack = BuiltResourcePack.of(writable, hash);
      return ResourcePackServer.server()
          .address("127.0.0.1", this.port)
          .pack(pack)
          .executor(Executors.newFixedThreadPool(8))
          .build();
    }
  }

  public void start() {
    this.server.start();
  }

  public void stop() {
    this.server.stop(0);
  }
}
