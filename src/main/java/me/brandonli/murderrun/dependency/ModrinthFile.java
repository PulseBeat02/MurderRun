/*
 * This file is part of Murder Run, a spin-off game-mode of Dead by Daylight
 * Copyright (C) Brandon Li <https://brandonli.me/>
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program.  If not, see <https://www.gnu.org/licenses/>.
 */
package me.brandonli.murderrun.dependency;

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
