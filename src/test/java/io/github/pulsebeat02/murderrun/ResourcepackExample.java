package io.github.pulsebeat02.murderrun;

import io.github.pulsebeat02.murderrun.resourcepack.provider.ServerPackHosting;

public final class ResourcepackExample {

  public static void main(final String[] args) {

    final ServerPackHosting daemon = new ServerPackHosting("localhost", 7270);
    daemon.buildPack();

    daemon.start();

    final String url = daemon.getUrl();
    System.out.println(url);

    Runtime.getRuntime().addShutdownHook(new Thread(daemon::stop));
  }
}
