package io.github.pulsebeat02.murderrun;

import io.github.pulsebeat02.murderrun.resourcepack.PackWrapper;
import java.io.IOException;
import java.nio.file.Path;

public class ResourcePackTest {

  public static void main(final String[] args) throws IOException {
    final Path path = Path.of(System.getProperty("user.dir"), "pack-testing.zip");
    final PackWrapper wrapper = new PackWrapper(path);
    wrapper.wrapPack();
  }
}
