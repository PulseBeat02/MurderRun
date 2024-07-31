package io.github.pulsebeat02.murderrun.utils;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;

public final class FileUtils {

  private FileUtils() {
    throw new UnsupportedOperationException("Utility class cannot be instantiated");
  }

  public static void createFile(final Path path) {

    final Path parent = path.getParent();
    if (parent == null) {
      throw new AssertionError("Failed to retrieve parent folder!");
    }

    try {
      Files.createDirectories(parent);
      if (Files.notExists(path)) {
        Files.createFile(path);
      }
    } catch (IOException e) {
      throw new RuntimeException(e);
    }
  }
}
