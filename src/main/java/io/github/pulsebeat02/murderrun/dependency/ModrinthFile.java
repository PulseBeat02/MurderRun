package io.github.pulsebeat02.murderrun.dependency;

import java.util.Set;

public final class ModrinthFile {

  private static final Set<String> VALID_LOADERS = Set.of("bukkit", "spigot", "paper");

  private final String url;
  private final String filename;
  private final boolean primary;
  private final long size;

  public ModrinthFile(
      final String url, final String filename, final boolean primary, final long size) {
    this.url = url;
    this.filename = filename;
    this.primary = primary;
    this.size = size;
  }

  public boolean isValidFile() {
    return this.isFileJar() && this.isBukkitPlugin();
  }

  public boolean isFileJar() {
    return this.filename.endsWith(".jar");
  }

  public boolean isBukkitPlugin() {
    final String lower = this.filename.toLowerCase();
    return VALID_LOADERS.stream().anyMatch(lower::contains);
  }

  public String getUrl() {
    return this.url;
  }

  public String getFilename() {
    return this.filename;
  }

  public boolean isPrimary() {
    return this.primary;
  }

  public long getSize() {
    return this.size;
  }
}
