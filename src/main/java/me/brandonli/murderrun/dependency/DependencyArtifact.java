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

import java.net.URI;
import org.checkerframework.checker.nullness.qual.Nullable;

public final class DependencyArtifact {

  private final String fileName;
  private final URI uri;
  private final @Nullable String sha512;

  public DependencyArtifact(final String fileName, final URI uri, final @Nullable String sha512) {
    this.fileName = fileName;
    this.uri = uri;
    this.sha512 = sha512;
  }

  public String getFileName() {
    return this.fileName;
  }

  public URI getUri() {
    return this.uri;
  }

  public @Nullable String getSha512() {
    return this.sha512;
  }
}
