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
package me.brandonli.murderrun.resourcepack;

import com.google.gson.Gson;
import com.google.gson.JsonParseException;
import java.io.IOException;
import java.io.Reader;
import java.io.Writer;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpHeaders;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.attribute.FileTime;
import java.time.Duration;
import java.util.ArrayList;
import java.util.List;
import me.brandonli.murderrun.utils.IOUtils;
import org.checkerframework.checker.initialization.qual.UnderInitialization;
import org.checkerframework.checker.nullness.qual.Nullable;

public final class PackHashCache {

  private static final String CACHE_FILE = "pack-hashes.json";
  private static final String UNKNOWN_VERSION = "";
  private static final Duration HEAD_TIMEOUT = Duration.ofSeconds(10);
  private static final Gson GSON = new Gson();

  private final Path path;
  private final List<PackHashEntry> entries;

  public PackHashCache() {
    final Path data = IOUtils.getPluginDataFolderPath();
    this.path = data.resolve(CACHE_FILE);
    this.entries = this.load(this.path);
  }

  public String getFileHash(final Path file) {
    final Path absolute = file.toAbsolutePath();
    final String source = absolute.toString();
    try {
      final long size = Files.size(absolute);
      final FileTime time = Files.getLastModifiedTime(absolute);
      final long modified = time.toMillis();
      final String version = String.valueOf(modified);
      final @Nullable PackHashEntry cached = this.find(source, size, version);
      if (cached != null) {
        return cached.getHash();
      }
      final String hash = IOUtils.getSHA1Hash(absolute);
      this.store(new PackHashEntry(source, size, version, hash));
      return hash;
    } catch (final IOException e) {
      throw new AssertionError(e);
    }
  }

  public String getRemoteHash(final URI uri) {
    final String source = uri.toString();
    final @Nullable PackHashEntry remote = this.describeRemote(uri);
    if (remote != null) {
      final long size = remote.getSize();
      final String version = remote.getVersion();
      final @Nullable PackHashEntry cached = this.find(source, size, version);
      if (cached != null) {
        return cached.getHash();
      }
    }
    final String hash = IOUtils.getSHA1Hash(uri);
    final long size = remote == null ? -1L : remote.getSize();
    final String version = remote == null ? UNKNOWN_VERSION : remote.getVersion();
    this.store(new PackHashEntry(source, size, version, hash));
    return hash;
  }

  public @Nullable String getCachedRemoteHash(final URI uri) {
    final String source = uri.toString();
    for (final PackHashEntry entry : this.entries) {
      final String entrySource = entry.getSource();
      if (entrySource.equals(source)) {
        return entry.getHash();
      }
    }
    return null;
  }

  private @Nullable PackHashEntry describeRemote(final URI uri) {
    try (final HttpClient client =
        HttpClient.newBuilder().followRedirects(HttpClient.Redirect.NORMAL).build()) {
      final HttpRequest request = HttpRequest.newBuilder()
          .uri(uri)
          .method("HEAD", HttpRequest.BodyPublishers.noBody())
          .timeout(HEAD_TIMEOUT)
          .build();
      final HttpResponse.BodyHandler<Void> handler = HttpResponse.BodyHandlers.discarding();
      final HttpResponse<Void> response = client.send(request, handler);
      final int status = response.statusCode();
      if (status < 200 || status >= 300) {
        return null;
      }
      final HttpHeaders headers = response.headers();
      final @Nullable String length = headers.firstValue("Content-Length").orElse(null);
      final @Nullable String etag = headers.firstValue("ETag").orElse(null);
      final @Nullable String lastModified = headers.firstValue("Last-Modified").orElse(null);
      final @Nullable String version = etag != null ? etag : lastModified;
      if (length == null || version == null) {
        return null;
      }
      final long size = Long.parseLong(length);
      final String source = uri.toString();
      return new PackHashEntry(source, size, version, UNKNOWN_VERSION);
    } catch (final IOException | NumberFormatException e) {
      return null;
    } catch (final InterruptedException e) {
      final Thread thread = Thread.currentThread();
      thread.interrupt();
      return null;
    }
  }

  private @Nullable PackHashEntry find(final String source, final long size, final String version) {
    if (version.equals(UNKNOWN_VERSION)) {
      return null;
    }
    for (final PackHashEntry entry : this.entries) {
      if (entry.matches(source, size, version)) {
        return entry;
      }
    }
    return null;
  }

  private void store(final PackHashEntry entry) {
    final String source = entry.getSource();
    this.entries.removeIf(existing -> existing.getSource().equals(source));
    this.entries.add(entry);
    this.save();
  }

  private List<PackHashEntry> load(@UnderInitialization PackHashCache this, final Path path) {
    if (IOUtils.createFile(path)) {
      return new ArrayList<>();
    }
    try (final Reader reader = Files.newBufferedReader(path)) {
      final PackHashEntry @Nullable [] stored = GSON.fromJson(reader, PackHashEntry[].class);
      if (stored == null) {
        return new ArrayList<>();
      }
      final List<PackHashEntry> entries = new ArrayList<>();
      for (final PackHashEntry entry : stored) {
        if (entry != null
            && entry.getSource() != null
            && entry.getVersion() != null
            && entry.getHash() != null) {
          entries.add(entry);
        }
      }
      return entries;
    } catch (final IOException | JsonParseException e) {
      return new ArrayList<>();
    }
  }

  private void save() {
    try (final Writer writer = Files.newBufferedWriter(this.path)) {
      GSON.toJson(this.entries, writer);
    } catch (final IOException e) {
      throw new AssertionError(e);
    }
  }
}
