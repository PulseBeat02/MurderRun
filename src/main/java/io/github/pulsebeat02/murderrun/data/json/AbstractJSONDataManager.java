package io.github.pulsebeat02.murderrun.data.json;

import static java.util.Objects.requireNonNull;

import com.google.common.reflect.TypeToken;
import com.google.gson.Gson;
import io.github.pulsebeat02.murderrun.data.yaml.ConfigurationManager;
import io.github.pulsebeat02.murderrun.gson.GsonProvider;
import io.github.pulsebeat02.murderrun.utils.ExecutorUtils;
import io.github.pulsebeat02.murderrun.utils.IOUtils;
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
    requireNonNull(manager);
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
