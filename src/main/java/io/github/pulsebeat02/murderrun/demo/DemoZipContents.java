/*

MIT License

Copyright (c) 2025 Brandon Li

Permission is hereby granted, free of charge, to any person obtaining a copy
of this software and associated documentation files (the "Software"), to deal
in the Software without restriction, including without limitation the rights
to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
copies of the Software, and to permit persons to whom the Software is
furnished to do so, subject to the following conditions:

The above copyright notice and this permission notice shall be included in all
copies or substantial portions of the Software.

THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE
SOFTWARE.

*/
package io.github.pulsebeat02.murderrun.demo;

import io.github.pulsebeat02.murderrun.MurderRun;
import io.github.pulsebeat02.murderrun.utils.IOUtils;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
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
