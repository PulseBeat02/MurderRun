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

import org.checkerframework.checker.nullness.qual.Nullable;

public final class ModrinthFile {

  private final @Nullable String url;
  private final @Nullable String filename;
  private final boolean primary;
  private final @Nullable ModrinthHashes hashes;

  public ModrinthFile(
      final @Nullable String url,
      final @Nullable String filename,
      final boolean primary,
      final @Nullable ModrinthHashes hashes) {
    this.url = url;
    this.filename = filename;
    this.primary = primary;
    this.hashes = hashes;
  }

  public boolean isJar() {
    return this.url != null && this.filename != null && this.filename.endsWith(".jar");
  }

  public @Nullable String getSha512() {
    return this.hashes == null ? null : this.hashes.getSha512();
  }

  public @Nullable String getUrl() {
    return this.url;
  }

  public @Nullable String getFilename() {
    return this.filename;
  }

  public boolean isPrimary() {
    return this.primary;
  }

  public @Nullable ModrinthHashes getHashes() {
    return this.hashes;
  }
}
