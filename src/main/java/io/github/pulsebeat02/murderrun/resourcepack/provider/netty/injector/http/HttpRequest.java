package io.github.pulsebeat02.murderrun.resourcepack.provider.netty.injector.http;

import io.netty.buffer.ByteBuf;
import io.netty.buffer.ByteBufInputStream;
import org.checkerframework.checker.nullness.qual.Nullable;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.HashMap;
import java.util.Map;

public final class HttpRequest {

  private final String request;
  private final Map<String, String> headers;
  private final String[] requestParts;
  public final String requestMethod;
  public final String requestURI;
  public final String protocolVersion;

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
      throw new RuntimeException(e);
    }
  }

  private static HttpRequest parse(final InputStream stream) {
    try (final InputStreamReader reader = new InputStreamReader(stream); final BufferedReader bufferedReader = new BufferedReader(reader)) {
      final String request = bufferedReader.readLine();
      final String line = request == null ? "" : request;
      final Map<String, String> headers = readHeaders(bufferedReader);
      return new HttpRequest(line, headers);
    } catch (final Exception e) {
      throw new RuntimeException(e);
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
      throw new RuntimeException(e);
    }
    return headers;
  }
}
