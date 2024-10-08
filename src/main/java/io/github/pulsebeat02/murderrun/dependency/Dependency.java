package io.github.pulsebeat02.murderrun.dependency;

import java.nio.file.Path;

public interface Dependency {
  String getName();

  String getVersion();

  Path getParentDirectory();

  Path download();
}
