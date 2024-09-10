package io.github.pulsebeat02.murderrun.dependency;

import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.nio.file.Path;
import java.util.Optional;
import java.util.concurrent.CompletableFuture;

public final class ModrinthDependency extends PluginDependency {

  public ModrinthDependency(final String name, final Path parent) {
    super(name, parent);
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
    if (json != null) {
      final ModrinthVersion[] versions = ModrinthVersion.serializeVersions(json);
      for (final ModrinthVersion version : versions) {
        final Optional<ModrinthFile> file = version.findFirstValidFile();
        if (file.isPresent()) {
          final ModrinthFile modrinthFile = file.get();
          return this.downloadJar(modrinthFile).join();
        }
      }
    }
    throw new AssertionError("Failed to download dependency!");
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
      return client
          .sendAsync(request, HttpResponse.BodyHandlers.ofFile(finalPath))
          .thenApplyAsync(HttpResponse::body);
    }
  }
}
