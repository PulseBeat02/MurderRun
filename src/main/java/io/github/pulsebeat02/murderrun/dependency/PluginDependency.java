package io.github.pulsebeat02.murderrun.dependency;

import java.nio.file.Path;

public abstract class PluginDependency implements Dependency {

  private final String name;
  private final Path parentDirectory;

  public PluginDependency(final String name, final Path parentDirectory) {
    this.name = name;
    this.parentDirectory = parentDirectory;
  }

  @Override
  public String getName() {
    return this.name;
  }

  @Override
  public Path getParentDirectory() {
    return this.parentDirectory;
  }
}
