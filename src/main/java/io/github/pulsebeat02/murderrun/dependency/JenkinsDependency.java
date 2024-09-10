package io.github.pulsebeat02.murderrun.dependency;

import static java.util.Objects.requireNonNull;

import java.io.IOException;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.nio.file.Path;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

public final class JenkinsDependency extends PluginDependency {

  private final String url;

  public JenkinsDependency(final String name, final Path parent, final String url) {
    super(name, parent);
    this.url = url;
  }

  @Override
  public Path download() {
    final String builtUrl = "%s/lastSuccessfulBuild/artifact/dist/target/".formatted(this.url);
    try {
      final Document doc = Jsoup.connect(builtUrl).get();
      final Elements links = doc.select("a[href$=.jar]");
      if (!links.isEmpty()) {
        final Element link = requireNonNull(links.first());
        final String jarUrl = link.absUrl("href");
        return this.downloadJar(jarUrl);
      }
    } catch (final IOException e) {
      throw new AssertionError(e);
    }
    throw new AssertionError("No jar file found!");
  }

  private Path downloadJar(final String jarUrl) {
    final Path parent = this.getParentDirectory();
    final Path filePath = parent.resolve("Citizens2.jar");
    try (final HttpClient client = HttpClient.newHttpClient()) {
      final HttpRequest request = HttpRequest.newBuilder()
          .uri(URI.create(jarUrl))
          .header("User-Agent", "PulseBeat02/murderrun")
          .header("Accept", "application/json")
          .GET()
          .build();
      client.sendAsync(request, HttpResponse.BodyHandlers.ofFile(filePath)).join();
      return filePath;
    }
  }
}
