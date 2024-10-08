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
      final String number = version.getVersionNumber();
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
      final HttpRequest request = HttpRequest.newBuilder()
        .uri(URI.create(fileUrl))
        .header("User-Agent", "PulseBeat02/murderrun")
        .header("Accept", "application/json")
        .GET()
        .build();
      return client.sendAsync(request, HttpResponse.BodyHandlers.ofFile(finalPath)).thenApplyAsync(HttpResponse::body);
    }
  }
}
