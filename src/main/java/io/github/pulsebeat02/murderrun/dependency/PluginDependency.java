package io.github.pulsebeat02.murderrun.dependency;

import java.nio.file.Path;

public abstract class PluginDependency implements Dependency {

  private final String name;
  private final String version;
  private final Path parentDirectory;

  public PluginDependency(final String name, final String version, final Path parentDirectory) {
    this.name = name;
    this.version = version;
    this.parentDirectory = parentDirectory;
  }

  @Override
  public String getName() {
    return this.name;
  }

  @Override
  public String getVersion() {
    return this.version;
  }

  @Override
  public Path getParentDirectory() {
    return this.parentDirectory;
  }
}
