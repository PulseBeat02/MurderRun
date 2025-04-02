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
package me.brandonli.murderrun.game.map;

import java.util.Collection;
import java.util.Map;
import me.brandonli.murderrun.MurderRun;
import me.brandonli.murderrun.game.arena.Arena;
import me.brandonli.murderrun.game.arena.ArenaManager;
import me.brandonli.murderrun.game.lobby.Lobby;
import me.brandonli.murderrun.game.lobby.LobbyManager;

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
