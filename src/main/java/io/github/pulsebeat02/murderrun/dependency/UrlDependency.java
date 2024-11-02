package io.github.pulsebeat02.murderrun.dependency;

import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.nio.file.Path;

public final class UrlDependency extends PluginDependency {

  private final String url;

  public UrlDependency(final String name, final String version, final String url) {
    super(name, version);
    this.url = url;
  }

  @Override
  public Path download() {
    try (final HttpClient client = HttpClient.newBuilder().followRedirects(HttpClient.Redirect.ALWAYS).build()) {
      final String name = this.getVersion();
      final String jar = name + ".jar";
      final Path parent = this.getParentDirectory();
      final Path finalPath = parent.resolve(jar);
      final URI uri = URI.create(this.url);
      final HttpRequest request = HttpRequest.newBuilder()
        .uri(uri)
        .GET()
        .build();
      final HttpResponse.BodyHandler<Path> bodyHandler = HttpResponse.BodyHandlers.ofFile(finalPath);
      final HttpResponse<Path> result = client.sendAsync(request, bodyHandler).join();
      return result.body();
    }
  }
}
