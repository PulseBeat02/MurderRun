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
package me.brandonli.murderrun.data.json;

import static java.util.Objects.requireNonNull;

import com.google.common.reflect.TypeToken;
import com.google.gson.Gson;
import java.io.IOException;
import java.io.Reader;
import java.io.Writer;
import java.lang.reflect.Type;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantReadWriteLock;
import me.brandonli.murderrun.data.yaml.ConfigurationManager;
import me.brandonli.murderrun.utils.ExecutorUtils;
import me.brandonli.murderrun.utils.IOUtils;
import me.brandonli.murderrun.utils.gson.GsonProvider;

public abstract class AbstractJSONDataManager<T> implements ConfigurationManager<T> {

  private static final byte[] EMPTY_JSON_BYTES = "{}".getBytes();

  private final transient TypeToken<T> token = new TypeToken<>(this.getClass()) {};

  private final ExecutorService service;
  private final Path json;
  private final Lock readLock;
  private final Lock writeLock;

  public AbstractJSONDataManager(final String name) {
    final Path parent = IOUtils.getPluginDataFolderPath();
    final ReentrantReadWriteLock lock = new ReentrantReadWriteLock();
    this.service = Executors.newVirtualThreadPerTaskExecutor();
    this.json = parent.resolve(name);
    this.readLock = lock.readLock();
    this.writeLock = lock.writeLock();
  }

  @Override
  public synchronized void serialize(final T manager) {
    if (manager == null) {
      return;
    }
    CompletableFuture.runAsync(() -> this.writeJson(manager), this.service);
  }

  @Override
  public synchronized void shutdown() {
    ExecutorUtils.shutdownExecutorGracefully(this.service);
  }

  private void writeJson(final T manager) {
    requireNonNull(manager); // checker framework
    this.writeLock.lock();
    try (final Writer writer = Files.newBufferedWriter(this.json)) {
      this.createFolders();
      final Gson gson = GsonProvider.getGson();
      gson.toJson(manager, writer);
    } catch (final IOException e) {
      throw new AssertionError(e);
    } finally {
      this.writeLock.unlock();
    }
  }

  private void createFolders() {
    try {
      if (Files.notExists(this.json)) {
        IOUtils.createFile(this.json);
        Files.write(this.json, EMPTY_JSON_BYTES);
      }
    } catch (final IOException e) {
      throw new AssertionError(e);
    }
  }

  @Override
  public synchronized T deserialize() {
    this.readLock.lock();
    this.createFolders();
    try (final Reader reader = Files.newBufferedReader(this.json)) {
      final Gson gson = GsonProvider.getGson();
      final Type type = this.token.getType();
      return gson.fromJson(reader, type);
    } catch (final IOException e) {
      throw new AssertionError(e);
    } finally {
      this.readLock.unlock();
    }
  }
}
