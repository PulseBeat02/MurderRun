package io.github.pulsebeat02.murderrun.dependency;

import io.github.pulsebeat02.murderrun.utils.IOUtils;

import java.io.IOException;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.nio.file.Path;

public final class UrlDependency extends PluginDependency {

  private final String url;
  private int tries;

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
      final HttpRequest request = HttpRequest.newBuilder().uri(uri)
              .header("User-Agent", "Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/116.0.0.0 Safari/537.36")
              .header("Accept", "text/html,application/xhtml+xml,application/xml;q=0.9,image/webp,*/*;q=0.8")
              .header("Accept-Language", "en-US,en;q=0.5")
              .GET()
              .build();
      final HttpResponse.BodyHandler<Path> bodyHandler = HttpResponse.BodyHandlers.ofFile(finalPath);
      final HttpResponse<Path> result = client.send(request, bodyHandler);
      final int code = result.statusCode();
      if (code != 200) {
        if (this.tries < 3) {
          this.tries++;
          return this.download();
        }
      }
      return finalPath;
    } catch (final IOException | InterruptedException e) {
      final Thread thread = Thread.currentThread();
      thread.interrupt();
      throw new AssertionError(e);
    }
  }
}
