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

import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import me.brandonli.murderrun.utils.IOUtils;

public final class PackWrapper {

  private final Path path;

  public PackWrapper() {
    final Path data = IOUtils.getPluginDataFolderPath();
    this.path = data.resolve("pack.zip");
  }

  public Path wrapPack() throws IOException {
    if (Files.exists(this.path)) {
      return this.path;
    }
    try (final InputStream stream = IOUtils.getResourceAsStream("pack.zip")) {
      Files.copy(stream, this.path);
    }
    return this.path;
  }
}
