package io.github.pulsebeat02.murderrun.data;

import static java.util.Objects.requireNonNull;

import com.google.gson.Gson;
import io.github.pulsebeat02.murderrun.MurderRun;
import io.github.pulsebeat02.murderrun.json.GsonProvider;
import io.github.pulsebeat02.murderrun.utils.ResourceUtils;
import java.io.IOException;
import java.io.Reader;
import java.io.Writer;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.concurrent.CompletableFuture;

public abstract class DataConfigurationManager<T> {

  private final Class<T> clazz;
  private final Path json;

  public DataConfigurationManager(final MurderRun plugin, final Class<T> clazz, final String name) {
    this.clazz = clazz;
    this.json = plugin.getDataFolder().toPath().resolve(name);
  }

  public void serialize(final T manager) {
    requireNonNull(manager);
    CompletableFuture.runAsync(() -> this.writeJson(manager));
  }

  private void writeJson(final T manager) {
    requireNonNull(manager); // checker framework
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
      ResourceUtils.createFile(this.json);
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
