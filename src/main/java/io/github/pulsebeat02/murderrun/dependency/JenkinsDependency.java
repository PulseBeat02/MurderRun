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
      final URI uri = URI.create(jarUrl);
      final HttpRequest request = HttpRequest.newBuilder().uri(uri).GET().build();
      final HttpResponse.BodyHandler<Path> bodyHandler = HttpResponse.BodyHandlers.ofFile(filePath);
      client.sendAsync(request, bodyHandler).join();
      return filePath;
    }
  }
}
