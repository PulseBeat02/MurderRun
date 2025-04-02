/*

MIT License

Copyright (c) 2025 Brandon Li

Permission is hereby granted, free of charge, to any person obtaining a copy
of this software and associated documentation files (the "Software"), to deal
in the Software without restriction, including without limitation the rights
to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
copies of the Software, and to permit persons to whom the Software is
furnished to do so, subject to the following conditions:

The above copyright notice and this permission notice shall be included in all
copies or substantial portions of the Software.

THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE
SOFTWARE.

*/
package io.github.pulsebeat02.murderrun.game.map;

import io.github.pulsebeat02.murderrun.MurderRun;
import io.github.pulsebeat02.murderrun.game.arena.Arena;
import io.github.pulsebeat02.murderrun.game.arena.ArenaManager;
import io.github.pulsebeat02.murderrun.game.lobby.Lobby;
import io.github.pulsebeat02.murderrun.game.lobby.LobbyManager;
import java.util.Collection;
import java.util.Map;

public final class SchematicLoader {

  private final MurderRun plugin;

  public SchematicLoader(final MurderRun plugin) {
    this.plugin = plugin;
  }

  public void loadSchematics() {
    this.loadArenaSchematics();
    this.loadLobbySchematics();
  }

  private void loadLobbySchematics() {
    final LobbyManager arenaManager = this.plugin.getLobbyManager();
    final Map<String, Lobby> lobbies = arenaManager.getLobbies();
    final Collection<Lobby> values = lobbies.values();
    for (final Lobby lobby : values) {
      final Schematic schematic = lobby.getSchematic();
      schematic.loadSchematicIntoMemory();
    }
  }

  private void loadArenaSchematics() {
    final ArenaManager arenaManager = this.plugin.getArenaManager();
    final Map<String, Arena> arenas = arenaManager.getArenas();
    final Collection<Arena> values = arenas.values();
    for (final Arena arena : values) {
      final Schematic schematic = arena.getSchematic();
      schematic.loadSchematicIntoMemory();
    }
  }
}
