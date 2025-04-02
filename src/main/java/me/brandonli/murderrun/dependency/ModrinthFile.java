/*

MIT License

Copyright (c) 2024 Brandon Li

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
package me.brandonli.murderrun.dependency;

import java.util.Set;

public final class ModrinthFile {

  private static final Set<String> VALID_LOADERS = Set.of("bukkit", "spigot", "paper");

  private final String url;
  private final String filename;
  private final boolean primary;
  private final long size;

  public ModrinthFile(final String url, final String filename, final boolean primary, final long size) {
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
