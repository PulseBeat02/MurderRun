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
package me.brandonli.murderrun.game.lobby.player;

import static java.util.Objects.requireNonNull;

import java.util.HashMap;
import java.util.Map;
import me.brandonli.murderrun.MurderRun;
import me.brandonli.murderrun.game.GameProperties;
import org.bukkit.entity.Player;
import org.checkerframework.checker.nullness.qual.Nullable;

public final class PlayerSelectionManager {

  private final MurderRun plugin;
  private final Map<Player, PlayerSelection> selections;
  private final GameProperties properties;

  public PlayerSelectionManager(final MurderRun plugin, final GameProperties properties) {
    this.plugin = plugin;
    this.selections = new HashMap<>();
    this.properties = properties;
  }

  public void addSelection(final Player player, final boolean killer) {
    final PlayerSelection selection = new PlayerSelection(this.plugin, this.properties, player, killer);
    this.selections.put(player, selection);
  }

  public @Nullable PlayerSelection getSelection(final Player player) {
    return this.selections.get(player);
  }

  public void removeSelection(final Player player) {
    this.selections.remove(player);
  }

  public PlayerSelection getOrCreateSelection(final Player player, final boolean killer) {
    if (!this.selections.containsKey(player)) {
      this.addSelection(player, killer);
    }
    return requireNonNull(this.getSelection(player));
  }
}
