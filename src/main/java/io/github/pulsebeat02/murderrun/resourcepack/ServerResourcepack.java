package io.github.pulsebeat02.murderrun.resourcepack;

import java.io.IOException;
import java.nio.file.Path;

public final class ServerResourcepack {

  private final Path path;

  public ServerResourcepack(final Path path) {
    this.path = path;
  }

  public void build() throws IOException {
    final PackWrapper wrapper = new PackWrapper(this.path);
    wrapper.wrapPack();
  }

  public Path getPath() {
    return this.path;
  }
}
