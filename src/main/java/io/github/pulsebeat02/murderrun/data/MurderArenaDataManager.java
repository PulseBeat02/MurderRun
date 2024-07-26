package io.github.pulsebeat02.murderrun.data;

import com.google.gson.Gson;
import io.github.pulsebeat02.murderrun.MurderRun;
import io.github.pulsebeat02.murderrun.arena.MurderArenaManager;
import io.github.pulsebeat02.murderrun.json.GsonProvider;
import java.io.File;
import java.io.IOException;
import java.io.Reader;
import java.io.Writer;
import java.nio.file.Files;
import java.nio.file.Path;

public final class MurderArenaDataManager {

  private final Path arenaJson;

  public MurderArenaDataManager(final MurderRun run) {
    final File file = run.getDataFolder();
    final Path path = file.toPath();
    this.arenaJson = path.resolve("arenas.json");
  }

  public Path getArenaJson() {
    return this.arenaJson;
  }

  public void serialize(final MurderArenaManager manager) {
    try (final Writer writer = Files.newBufferedWriter(this.arenaJson)) {
      this.createFolders();
      final Gson gson = GsonProvider.getGson();
      gson.toJson(manager, writer);
    } catch (final IOException e) {
      throw new AssertionError(e);
    }
  }

  private void createFolders() throws IOException {
    final Path parent = this.arenaJson.getParent();
    if (parent == null) {
      throw new AssertionError("Unable to get parent folder!");
    }
    Files.createDirectories(parent);
    Files.createFile(this.arenaJson);
  }

  public MurderArenaManager deserialize() {
    try (final Reader reader = Files.newBufferedReader(this.arenaJson)) {
      this.createFolders();
      final Gson gson = GsonProvider.getGson();
      return gson.fromJson(reader, MurderArenaManager.class);
    } catch (final IOException e) {
      throw new AssertionError(e);
    }
  }
}
