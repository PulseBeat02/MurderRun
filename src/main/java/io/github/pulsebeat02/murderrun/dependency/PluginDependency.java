package io.github.pulsebeat02.murderrun.dependency;

import static java.util.Objects.requireNonNull;

import io.github.pulsebeat02.murderrun.utils.IOUtils;
import java.nio.file.Path;

public abstract class PluginDependency implements Dependency {

  private final String name;
  private final String version;
  private final Path parentDirectory;

  public PluginDependency(final String name, final String version) {
    final Path data = IOUtils.getPluginDataFolderPath();
    this.name = name;
    this.version = version;
    this.parentDirectory = requireNonNull(data.getParent());
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
