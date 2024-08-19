package io.github.pulsebeat02.murderrun;

import io.github.pulsebeat02.murderrun.resourcepack.server.ResourcePackDaemon;

public final class ResourcepackExample {

  public static void main(final String[] args) {

    final ResourcePackDaemon daemon = new ResourcePackDaemon("localhost", 7270);
    daemon.buildPack();

    daemon.start();

    final String url = daemon.getUrl();
    System.out.println(url);

    Runtime.getRuntime().addShutdownHook(new Thread(daemon::stop));
  }
}
