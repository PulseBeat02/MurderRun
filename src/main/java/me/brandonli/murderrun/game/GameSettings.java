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
package me.brandonli.murderrun.game;

import me.brandonli.murderrun.game.arena.Arena;
import me.brandonli.murderrun.game.lobby.Lobby;
import org.bukkit.World;
import org.checkerframework.checker.nullness.qual.Nullable;

public final class GameSettings {

  private @Nullable Arena arena;
  private @Nullable Lobby lobby;
  private @Nullable World world;

  public @Nullable Lobby getLobby() {
    return this.lobby;
  }

  public void setLobby(final @Nullable Lobby lobbySpawn) {
    if (lobbySpawn != null) {
      this.lobby = new Lobby(lobbySpawn);
    }
  }

  public @Nullable Arena getArena() {
    return this.arena;
  }

  public void setArena(final @Nullable Arena arena) {
    if (arena != null) {
      this.arena = new Arena(arena);
    }
  }

  public @Nullable World getWorld() {
    return this.world;
  }

  public void setWorld(final @Nullable World world) {
    this.world = world;
  }
}
