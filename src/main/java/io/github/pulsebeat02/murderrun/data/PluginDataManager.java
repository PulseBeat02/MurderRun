package io.github.pulsebeat02.murderrun.data;

import com.google.gson.Gson;
import io.github.pulsebeat02.murderrun.MurderRun;
import io.github.pulsebeat02.murderrun.json.GsonProvider;
import io.github.pulsebeat02.murderrun.utils.FileUtils;
import java.io.IOException;
import java.io.Reader;
import java.io.Writer;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.concurrent.CompletableFuture;

public abstract class PluginDataManager<T> {

  private final Class<T> clazz;
  private final Path json;

  public PluginDataManager(final MurderRun plugin, final Class<T> clazz, final String name) {
    this.clazz = clazz;
    this.json = plugin.getDataFolder().toPath().resolve(name);
  }

  public Path getJson() {
    return this.json;
  }

  public void serialize(final T manager) {
    if (manager == null) {
      throw new AssertionError("Failed to serialize data manager!");
    }
    CompletableFuture.runAsync(() -> this.writeJson(manager));
  }

  @SuppressWarnings("nullness")
  private void writeJson(final T manager) {
    try (final Writer writer = Files.newBufferedWriter(this.json)) {
      this.createFolders();
      final Gson gson = GsonProvider.getGson();
      gson.toJson(manager, writer);
    } catch (final IOException e) {
      throw new AssertionError(e);
    }
  }

  private void createFolders() {
    try {
      FileUtils.createFile(this.json);
      Files.write(this.json, "{}".getBytes());
    } catch (final IOException e) {
      throw new AssertionError(e);
    }
  }

  public T deserialize() {
    this.createFolders();
    try (final Reader reader = Files.newBufferedReader(this.json)) {
      final Gson gson = GsonProvider.getGson();
      return gson.fromJson(reader, this.clazz);
    } catch (final IOException e) {
      throw new AssertionError(e);
    }
  }
}
