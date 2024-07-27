package io.github.pulsebeat02.murderrun.data;

import com.google.gson.Gson;
import io.github.pulsebeat02.murderrun.MurderRun;
import io.github.pulsebeat02.murderrun.json.GsonProvider;
import java.io.IOException;
import java.io.Reader;
import java.io.Writer;
import java.nio.file.Files;
import java.nio.file.Path;

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
    try (final Writer writer = Files.newBufferedWriter(this.json)) {
      this.createFolders();
      final Gson gson = GsonProvider.getGson();
      gson.toJson(manager, writer);
    } catch (final IOException e) {
      throw new AssertionError(e);
    }
  }

  private void createFolders() {

    final Path parent = this.json.getParent();
    if (parent == null) {
      throw new AssertionError("Failed to retrieve parent folder!");
    }

    try {
      Files.createDirectories(parent);
      if (Files.notExists(this.json)) {
        Files.createFile(this.json);
        Files.write(this.json, "{}".getBytes());
      }
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
