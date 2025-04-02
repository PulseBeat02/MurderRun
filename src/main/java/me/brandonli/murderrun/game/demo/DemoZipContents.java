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
package me.brandonli.murderrun.game.demo;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import me.brandonli.murderrun.MurderRun;
import me.brandonli.murderrun.utils.IOUtils;
import org.checkerframework.checker.initialization.qual.UnderInitialization;

public final class DemoZipContents {

  private static final String DEMO_ZIP_NAME = "demo-setup.zip";

  private final MurderRun plugin;
  private final Path folderPath;
  private final Path zipPath;

  public DemoZipContents(final MurderRun plugin) {
    this.plugin = plugin;
    this.folderPath = this.getFolderPath();
    this.zipPath = this.getZipPath();
  }

  private Path getFolderPath(@UnderInitialization DemoZipContents this) {
    final Path data = IOUtils.getPluginDataFolderPath();
    return data.resolve("demo-setup");
  }

  private Path getZipPath(@UnderInitialization DemoZipContents this) {
    final Path data = IOUtils.getPluginDataFolderPath();
    return data.resolve(DEMO_ZIP_NAME);
  }

  public void unzipContents() throws IOException {
    IOUtils.createFolder(this.folderPath);
    if (Files.notExists(this.zipPath)) {
      this.plugin.saveResource(DEMO_ZIP_NAME, true);
    }
    IOUtils.unzip(this.zipPath, this.folderPath);
  }

  public void deleteFolder() {
    IOUtils.deleteExistingDirectory(this.folderPath);
  }

  public Path getArenaTestSchematic() {
    return this.folderPath.resolve("ArenaTest");
  }

  public Path getLobbyTestSchematic() {
    return this.folderPath.resolve("LobbyTest");
  }

  public Path getArenaFolder() {
    return this.folderPath.resolve("ArenaTestWorld");
  }

  public Path getLobbyFolder() {
    return this.folderPath.resolve("LobbyTestWorld");
  }

  public Path getArenaJson() {
    return this.folderPath.resolve("arena.json");
  }

  public Path getLobbyJson() {
    return this.folderPath.resolve("lobby.json");
  }
}
