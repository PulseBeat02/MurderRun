package io.github.pulsebeat02.murderrun.data;

import com.google.gson.Gson;
import io.github.pulsebeat02.murderrun.MurderRun;
import io.github.pulsebeat02.murderrun.arena.MurderArenaManager;
import io.github.pulsebeat02.murderrun.json.GsonProvider;

import java.io.IOException;
import java.io.Reader;
import java.io.Writer;
import java.nio.file.Files;
import java.nio.file.Path;

public final class ArenaDataManager {

  private final Path arenaJson;

  public ArenaDataManager(final MurderRun run) {
    this.arenaJson = run.getDataPath().resolve("arenas.json");
  }

  private void createFolders() throws IOException {
    final Path parent = this.arenaJson.getParent();
    Files.createDirectories(parent);
    Files.createFile(this.arenaJson);
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