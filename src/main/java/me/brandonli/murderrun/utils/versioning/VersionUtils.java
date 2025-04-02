/*

MIT License

Copyright (c) 2025 Brandon Li

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
package me.brandonli.murderrun.utils.versioning;

import static java.util.Objects.requireNonNull;

import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;
import java.net.URL;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.security.CodeSource;
import java.security.ProtectionDomain;
import java.util.jar.Attributes;
import java.util.jar.JarFile;
import java.util.jar.Manifest;

public final class VersionUtils {

  private static final String GITHUB_API_URL = "https://api.github.com/repos/PulseBeat02/MurderRun/commits";
  private static final HttpClient HTTP_CLIENT = HttpClient.newHttpClient();

  private VersionUtils() {
    throw new UnsupportedOperationException("Utility class cannot be instantiated");
  }

  public static String getCurrentCommitFromManifest() {
    try {
      final Class<VersionUtils> clazz = VersionUtils.class;
      final ProtectionDomain protectionDomain = clazz.getProtectionDomain();
      final CodeSource codeSource = requireNonNull(protectionDomain.getCodeSource());
      final URL location = codeSource.getLocation();
      final URI uri = location.toURI();
      final String path = uri.getPath();
      try (final JarFile jarFile = new JarFile(path)) {
        final Manifest manifest = requireNonNull(jarFile.getManifest());
        final Attributes attributes = manifest.getMainAttributes();
        return attributes.getValue("Git-Commit");
      } catch (final IOException e) {
        throw new AssertionError(e);
      }
    } catch (final URISyntaxException e) {
      throw new AssertionError(e);
    }
  }

  public static String getLatestCommitFromGitHub() {
    try {
      final HttpResponse<String> response = getGitHubResponse();
      final String body = response.body();
      final JsonElement jsonElement = JsonParser.parseString(body);
      final JsonArray commits = jsonElement.getAsJsonArray();
      final JsonElement first = commits.get(0);
      final String sha = getCommit(first);
      return sha.substring(0, 8);
    } catch (final IOException | InterruptedException e) {
      final Thread current = Thread.currentThread();
      current.interrupt();
      throw new AssertionError(e);
    }
  }

  private static HttpResponse<String> getGitHubResponse() throws IOException, InterruptedException {
    final URI uri = URI.create(GITHUB_API_URL);
    final HttpResponse.BodyHandler<String> handler = HttpResponse.BodyHandlers.ofString();
    final HttpRequest request = HttpRequest.newBuilder().uri(uri).header("Accept", "application/vnd.github.v3+json").build();
    return HTTP_CLIENT.send(request, handler);
  }

  public static int getCommitsBehindCount(final String currentCommit) {
    try {
      final HttpResponse<String> response = getGitHubResponse();
      final String body = response.body();
      final JsonElement jsonElement = JsonParser.parseString(body);
      final JsonArray commits = jsonElement.getAsJsonArray();
      int count = 0;
      for (final JsonElement commit : commits) {
        final String sha = getCommit(commit);
        final String hash = sha.substring(0, 7);
        if (hash.equals(currentCommit)) {
          break;
        }
        count++;
      }
      return count;
    } catch (final IOException | InterruptedException e) {
      final Thread current = Thread.currentThread();
      current.interrupt();
      throw new AssertionError(e);
    }
  }

  private static String getCommit(final JsonElement commit) {
    final JsonObject commitObject = commit.getAsJsonObject();
    final JsonElement shaElement = commitObject.get("sha");
    return shaElement.getAsString();
  }
}
