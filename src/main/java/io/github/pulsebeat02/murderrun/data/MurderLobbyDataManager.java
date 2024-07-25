package io.github.pulsebeat02.murderrun.data;

import com.google.gson.Gson;
import io.github.pulsebeat02.murderrun.MurderRun;
import io.github.pulsebeat02.murderrun.json.GsonProvider;
import io.github.pulsebeat02.murderrun.lobby.MurderLobbyManager;
import java.io.File;
import java.io.IOException;
import java.io.Reader;
import java.io.Writer;
import java.nio.file.Files;
import java.nio.file.Path;

public final class MurderLobbyDataManager {

  private final Path lobbyJson;

  public MurderLobbyDataManager(final MurderRun run) {
    final File file = run.getDataFolder();
    final Path path = file.toPath();
    this.lobbyJson = path.resolve("lobbies.json");
  }

  public Path getLobbyJson() {
    return this.lobbyJson;
  }

  public void serialize(final MurderLobbyManager manager) {
    try (final Writer writer = Files.newBufferedWriter(this.lobbyJson)) {
      this.createFolders();
      final Gson gson = GsonProvider.getGson();
      gson.toJson(manager, writer);
    } catch (final IOException e) {
      throw new AssertionError(e);
    }
  }

  private void createFolders() throws IOException {
    final Path parent = this.lobbyJson.getParent();
    Files.createDirectories(parent);
    Files.createFile(this.lobbyJson);
  }

  public MurderLobbyManager deserialize() {
    try (final Reader reader = Files.newBufferedReader(this.lobbyJson)) {
      this.createFolders();
      final Gson gson = GsonProvider.getGson();
      return gson.fromJson(reader, MurderLobbyManager.class);
    } catch (final IOException e) {
      throw new AssertionError(e);
    }
  }
}
