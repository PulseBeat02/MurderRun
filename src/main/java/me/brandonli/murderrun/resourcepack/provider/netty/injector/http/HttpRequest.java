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
package me.brandonli.murderrun.resourcepack.provider.netty.injector.http;

import io.netty.buffer.ByteBuf;
import io.netty.buffer.ByteBufInputStream;
import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.HashMap;
import java.util.Map;
import org.checkerframework.checker.nullness.qual.Nullable;

public final class HttpRequest {

  private final String request;
  private final Map<String, String> headers;
  private final String[] requestParts;
  private final String requestMethod;
  private final String requestURI;
  private final String protocolVersion;

  public HttpRequest(final String request, final Map<String, String> headers) {
    this.request = request;
    this.headers = headers;
    this.requestParts = request.split(" ");
    this.requestMethod = this.requestParts[0];
    this.requestURI = this.requestParts[1];
    this.protocolVersion = this.requestParts[2];
  }

  public @Nullable String get(final String header) {
    return this.headers.get(header);
  }

  public static HttpRequest parse(final ByteBuf buf) {
    try (final ByteBufInputStream stream = new ByteBufInputStream(buf)) {
      return parse(stream);
    } catch (final Exception e) {
      throw new AssertionError(e);
    }
  }

  private static HttpRequest parse(final InputStream stream) {
    try (final InputStreamReader reader = new InputStreamReader(stream); final BufferedReader bufferedReader = new BufferedReader(reader)) {
      final String request = bufferedReader.readLine();
      final String line = request == null ? "" : request;
      final Map<String, String> headers = readHeaders(bufferedReader);
      return new HttpRequest(line, headers);
    } catch (final Exception e) {
      throw new AssertionError(e);
    }
  }

  private static Map<String, String> readHeaders(final BufferedReader reader) {
    final Map<String, String> headers = new HashMap<>();
    try {
      String header = reader.readLine();
      while (header != null && !header.isEmpty()) {
        final int split = header.indexOf(':');
        headers.put(header.substring(0, split), header.substring(split + 1).trim());
        header = reader.readLine();
      }
    } catch (final Exception e) {
      throw new AssertionError(e);
    }
    return headers;
  }

  public String getRequest() {
    return this.request;
  }

  public Map<String, String> getHeaders() {
    return this.headers;
  }

  public String[] getRequestParts() {
    return this.requestParts;
  }

  public String getRequestMethod() {
    return this.requestMethod;
  }

  public String getRequestURI() {
    return this.requestURI;
  }

  public String getProtocolVersion() {
    return this.protocolVersion;
  }
}
