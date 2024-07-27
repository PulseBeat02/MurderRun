package io.github.pulsebeat02.murderrun.utils;

import java.io.IOException;
import java.net.ServerSocket;

public final class NetworkUtils {

  private NetworkUtils() {
    throw new UnsupportedOperationException("Utility class cannot be instantiated");
  }

  public static int findNextAvailablePort() {
    try (final ServerSocket s = new ServerSocket(0)) {
      return s.getLocalPort();
    } catch (final IOException e) {
      throw new RuntimeException(e);
    }
  }
}
