package io.github.pulsebeat02.murderrun.utils;

import static java.util.Objects.requireNonNull;

import java.io.*;
import java.nio.file.Files;
import java.nio.file.Path;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

public final class ResourceUtils {

  private ResourceUtils() {
    throw new UnsupportedOperationException("Utility class cannot be instantiated");
  }

  public static Reader getResourceAsReader(final String name) {
    return new BufferedReader(new InputStreamReader(getResourceAsStream(name)));
  }

  public static InputStream getResourceAsStream(final String name) {
    final ClassLoader loader = requireNonNull(ResourceUtils.class.getClassLoader());
    return requireNonNull(loader.getResourceAsStream(name));
  }

  public static String getFilename(final String url) {
    return url.substring(url.lastIndexOf('/') + 1);
  }

  public static String createPackHash(final Path path)
      throws IOException, NoSuchAlgorithmException {
    final MessageDigest digest = MessageDigest.getInstance("SHA-1");
    try (final InputStream fis = Files.newInputStream(path)) {
      int n = 0;
      final byte[] buffer = new byte[8192];
      while (n != -1) {
        n = fis.read(buffer);
        if (n > 0) {
          digest.update(buffer, 0, n);
        }
      }
    }
    final byte[] hash = digest.digest();
    return new String(hash);
  }
}
