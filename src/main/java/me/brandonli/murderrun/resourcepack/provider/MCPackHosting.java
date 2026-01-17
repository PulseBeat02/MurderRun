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
package me.brandonli.murderrun.resourcepack.provider;

import com.google.gson.Gson;
import java.io.IOException;
import java.io.Reader;
import java.io.Writer;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Objects;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantReadWriteLock;
import me.brandonli.murderrun.MurderRun;
import me.brandonli.murderrun.utils.IOUtils;
import me.brandonli.murderrun.utils.gson.GsonProvider;
import org.checkerframework.checker.nullness.qual.Nullable;

public final class MCPackHosting extends ResourcePackProvider {

  private static final String WEBSITE_URL = "https://mc-packs.net";
  private static final String DOWNLOAD_WEBSITE_URL = "https://download.mc-packs.net";
  private static final String PACK_URL = "%s/pack/%s.zip";
  private static final HttpClient HTTP_CLIENT = HttpClient.newHttpClient();

  private String url;
  private PackInfo info;

  public MCPackHosting(final MurderRun plugin) {
    super(plugin, ProviderMethod.MC_PACK_HOSTING);
  }

  @Override
  public String getRawUrl() {
    return this.url;
  }

  @Override
  public void start() {
    super.start();
    final ReentrantReadWriteLock lock = new ReentrantReadWriteLock();
    final PackInfo info = this.checkFileUrl(lock);
    final Path zip = ResourcePackProvider.getServerPack();
    this.info = Objects.requireNonNullElseGet(info, () -> this.createNewPackInfo(zip));
    this.url = this.updateAndRetrievePackJSON(lock, this.info);
  }

  private PackInfo createNewPackInfo(final Path zip) {
    try {
      this.uploadPackPost(zip);
      final String hash = IOUtils.getSHA1Hash(zip);
      final String url = PACK_URL.formatted(DOWNLOAD_WEBSITE_URL, hash);
      return new PackInfo(url, 0);
    } catch (final IOException e) {
      throw new AssertionError(e);
    } catch (final InterruptedException e) {
      final Thread current = Thread.currentThread();
      current.interrupt();
      throw new AssertionError(e);
    }
  }

  private void uploadPackPost(final Path zip) throws IOException, InterruptedException {
    final byte[] fileBytes = Files.readAllBytes(zip);
    final HttpRequest.BodyPublisher bodyPublisher =
        HttpRequest.BodyPublishers.ofByteArray(fileBytes);
    final URI uri = URI.create(WEBSITE_URL);
    final HttpRequest request = HttpRequest.newBuilder()
        .uri(uri)
        .header("Content-Type", "application/json")
        .header("Accept", "*/*")
        .header("Accept-Encoding", "gzip, deflate, br, zstd")
        .header("Accept-Language", "en-US,en;q=0.9")
        .POST(bodyPublisher)
        .build();
    final HttpResponse.BodyHandler<String> bodyHandlers = HttpResponse.BodyHandlers.ofString();
    final HttpResponse<String> response = HTTP_CLIENT.send(request, bodyHandlers);
    final int status = response.statusCode();
    if (status != 200) {
      throw new IOException("Failed to upload file to MC-Packs.net! Status: " + status);
    }
  }

  private String updateAndRetrievePackJSON(final ReentrantReadWriteLock lock, final PackInfo info) {
    final Lock write = lock.writeLock();
    final Path path = this.getCachedFilePath();
    try (final Writer writer = Files.newBufferedWriter(path)) {
      final int loads = info.loads + 1;
      final PackInfo updated = new PackInfo(info.url, loads);
      final Gson gson = GsonProvider.getGson();
      write.lock();
      gson.toJson(updated, writer);
      return updated.url;
    } catch (final IOException e) {
      throw new AssertionError(e);
    } finally {
      write.unlock();
    }
  }

  private @Nullable PackInfo checkFileUrl(final ReentrantReadWriteLock lock) {
    final Path path = this.getCachedFilePath();
    if (IOUtils.createFile(path)) {
      return null;
    }

    final Lock read = lock.readLock();
    read.lock();

    try (final Reader reader = Files.newBufferedReader(path)) {
      final Gson gson = GsonProvider.getGson();
      final PackInfo info = gson.fromJson(reader, PackInfo.class);
      return info == null ? null : (info.loads > 10 ? null : info);
    } catch (final IOException e) {
      throw new AssertionError(e);
    } finally {
      read.unlock();
    }
  }

  private Path getCachedFilePath() {
    final Path data = IOUtils.getPluginDataFolderPath();
    return data.resolve("cached-packs.json");
  }

  record PackInfo(String url, int loads) {}
}
