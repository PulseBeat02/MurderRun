package io.github.pulsebeat02.murderrun.dependency;

import io.github.pulsebeat02.murderrun.utils.IOUtils;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.nio.file.Path;

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
      final HttpRequest request = HttpRequest.newBuilder()
        .uri(URI.create(jarUrl))
        .header("User-Agent", "PulseBeat02/murderrun")
        .header("Accept", "application/json")
        .GET()
        .build();
      final HttpResponse.BodyHandler<Path> bodyHandler = HttpResponse.BodyHandlers.ofFile(filePath);
      client.sendAsync(request, bodyHandler).join();
      return filePath;
    }
  }
}
