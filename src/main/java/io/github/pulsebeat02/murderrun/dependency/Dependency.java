package io.github.pulsebeat02.murderrun.dependency;

import java.nio.file.Path;

public interface Dependency {

  String getName();

  Path getParentDirectory();

  Path download();
}
