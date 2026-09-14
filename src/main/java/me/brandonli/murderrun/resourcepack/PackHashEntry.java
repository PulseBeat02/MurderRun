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
package me.brandonli.murderrun.resourcepack;

public final class PackHashEntry {

  private final String source;
  private final long size;
  private final String version;
  private final String hash;

  public PackHashEntry(
      final String source, final long size, final String version, final String hash) {
    this.source = source;
    this.size = size;
    this.version = version;
    this.hash = hash;
  }

  public boolean matches(final String source, final long size, final String version) {
    return this.source.equals(source) && this.size == size && this.version.equals(version);
  }

  public String getSource() {
    return this.source;
  }

  public long getSize() {
    return this.size;
  }

  public String getVersion() {
    return this.version;
  }

  public String getHash() {
    return this.hash;
  }
}
