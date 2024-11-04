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
package io.github.pulsebeat02.murderrun.dependency;

import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.nio.file.Path;
import java.util.Optional;
import java.util.concurrent.CompletableFuture;

public final class ModrinthDependency extends PluginDependency {

  public ModrinthDependency(final String name, final String version) {
    super(name, version);
  }

  @Override
  public Path download() {
    try (final HttpClient client = HttpClient.newHttpClient()) {
      final String name = this.getName();
      final HttpRequest request = HttpRequest.newBuilder()
        .uri(URI.create("https://api.modrinth.com/v2/project/%s/version".formatted(name)))
        .header("User-Agent", "PulseBeat02/murderrun")
        .header("Accept", "application/json")
        .GET()
        .build();
      return client
        .sendAsync(request, HttpResponse.BodyHandlers.ofString())
        .thenApplyAsync(HttpResponse::body)
        .thenApplyAsync(this::findValidFile)
        .exceptionally(e -> {
          throw new AssertionError(e);
        })
        .join();
    }
  }

  private Path findValidFile(final String json) {
    if (json == null) {
      throw new AssertionError("Failed to download dependency because JSON is empty!");
    }

    final ModrinthVersion[] versions = ModrinthVersion.serializeVersions(json);
    final String target = this.getVersion();
    for (final ModrinthVersion version : versions) {
      final String number = version.getId();
      if (!number.equals(target)) {
        continue;
      }

      final Optional<ModrinthFile> file = version.findFirstValidFile();
      if (file.isEmpty()) {
        continue;
      }

      final ModrinthFile modrinthFile = file.get();
      return this.downloadJar(modrinthFile).join();
    }

    throw new AssertionError("Failed to download dependency because no suitable version found!");
  }

  private CompletableFuture<Path> downloadJar(final ModrinthFile file) {
    final String fileUrl = file.getUrl();
    final String fileName = file.getFilename();
    final Path parent = this.getParentDirectory();
    final Path finalPath = parent.resolve(fileName);
    try (final HttpClient client = HttpClient.newHttpClient()) {
      final URI uri = URI.create(fileUrl);
      final HttpRequest request = HttpRequest.newBuilder().uri(uri).GET().build();
      final HttpResponse.BodyHandler<Path> bodyHandler = HttpResponse.BodyHandlers.ofFile(finalPath);
      return client.sendAsync(request, bodyHandler).thenApplyAsync(HttpResponse::body);
    }
  }
}
