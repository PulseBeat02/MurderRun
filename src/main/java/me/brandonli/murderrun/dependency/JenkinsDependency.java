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
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.nio.file.Path;
import me.brandonli.murderrun.utils.IOUtils;

public final class JenkinsDependency extends PluginDependency {

  private final String url;

  public JenkinsDependency(final String name, final String version, final String url) {
    super(name, version);
    this.url = url;
  }

  @Override
  public Path download() {
    final String version = this.getVersion();
    final String download = "%s/%s.jar".formatted(this.url, version);
    return this.downloadJar(download);
  }

  private Path downloadJar(final String jarUrl) {
    final Path parent = this.getParentDirectory();
    final String name = IOUtils.getFileName(jarUrl);
    final Path filePath = parent.resolve(name);
    try (final HttpClient client = HttpClient.newHttpClient()) {
      final URI uri = URI.create(jarUrl);
      final HttpRequest request = HttpRequest.newBuilder().uri(uri).GET().build();
      final HttpResponse.BodyHandler<Path> bodyHandler = HttpResponse.BodyHandlers.ofFile(filePath);
      client.sendAsync(request, bodyHandler).join();
      return filePath;
    }
  }
}
