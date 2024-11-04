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
      final HttpRequest request = HttpRequest.newBuilder().uri(uri).GET().build();
      final HttpResponse.BodyHandler<Path> bodyHandler = HttpResponse.BodyHandlers.ofFile(finalPath);
      final HttpResponse<Path> result = client.sendAsync(request, bodyHandler).join();
      return result.body();
    }
  }
}
