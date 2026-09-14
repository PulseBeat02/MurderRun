/*
 * This file is part of Murder Run, a spin-off game-mode of Dead by Daylight
 * Copyright (C) Brandon Li <https://brandonli.me/>
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program.  If not, see <https://www.gnu.org/licenses/>.
 */
package me.brandonli.murderrun.dependency;

import java.io.IOException;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.nio.file.Path;
import java.nio.file.StandardOpenOption;
import java.time.Duration;

public final class HttpDependencyClient implements DependencyClient {

  private static final String USER_AGENT = "MurderRun (+https://github.com/PulseBeat02/MurderRun)";
  private static final Duration CONNECT_TIMEOUT = Duration.ofSeconds(20);
  private static final Duration REQUEST_TIMEOUT = Duration.ofMinutes(5);

  private final HttpClient client;

  public HttpDependencyClient() {
    this.client = HttpClient.newBuilder()
        .followRedirects(HttpClient.Redirect.NORMAL)
        .connectTimeout(CONNECT_TIMEOUT)
        .build();
  }

  @Override
  public String getText(final URI uri) {
    final HttpRequest request = this.createRequest(uri);
    final HttpResponse.BodyHandler<String> handler = HttpResponse.BodyHandlers.ofString();
    final HttpResponse<String> response = this.send(request, handler);
    this.checkStatus(uri, response);
    return response.body();
  }

  @Override
  public void download(final URI uri, final Path destination) {
    final HttpRequest request = this.createRequest(uri);
    final HttpResponse.BodyHandler<Path> handler = HttpResponse.BodyHandlers.ofFile(
        destination,
        StandardOpenOption.CREATE,
        StandardOpenOption.WRITE,
        StandardOpenOption.TRUNCATE_EXISTING);
    final HttpResponse<Path> response = this.send(request, handler);
    this.checkStatus(uri, response);
  }

  @Override
  public void close() {
    this.client.close();
  }

  private HttpRequest createRequest(final URI uri) {
    return HttpRequest.newBuilder()
        .uri(uri)
        .header("User-Agent", USER_AGENT)
        .timeout(REQUEST_TIMEOUT)
        .GET()
        .build();
  }

  private <T> HttpResponse<T> send(
      final HttpRequest request, final HttpResponse.BodyHandler<T> handler) {
    final URI uri = request.uri();
    try {
      return this.client.send(request, handler);
    } catch (final IOException e) {
      final String message = "Failed to reach %s".formatted(uri);
      throw new DependencyException(message, e);
    } catch (final InterruptedException e) {
      final String message = "Interrupted while requesting %s".formatted(uri);
      throw new DependencyException(message, e);
    }
  }

  private void checkStatus(final URI uri, final HttpResponse<?> response) {
    final int status = response.statusCode();
    if (status < 200 || status >= 300) {
      final String message = "Received HTTP %d from %s".formatted(status, uri);
      throw new DependencyException(message);
    }
  }
}
