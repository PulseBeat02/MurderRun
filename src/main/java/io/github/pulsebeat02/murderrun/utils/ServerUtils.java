package io.github.pulsebeat02.murderrun.utils;

public final class ServerUtils {

  private static final boolean PAPER_SERVER;

  static {
    boolean temp;
    try {
      Class.forName("com.destroystokyo.paper.util.VersionFetcher");
      temp = true;
    } catch (final ClassNotFoundException ignored) {
      temp = false;
    }
    PAPER_SERVER = temp;
  }

  private ServerUtils() {
    throw new UnsupportedOperationException("Utility class cannot be instantiated");
  }

  public static boolean isPaperServer() {
    return PAPER_SERVER;
  }
}
