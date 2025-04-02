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
package me.brandonli.murderrun.game.lobby.player;

import static java.util.Objects.requireNonNull;

import java.util.HashMap;
import java.util.Map;
import me.brandonli.murderrun.MurderRun;
import org.bukkit.entity.Player;
import org.checkerframework.checker.nullness.qual.Nullable;

public final class PlayerSelectionManager {

  private final MurderRun plugin;
  private final Map<Player, PlayerSelection> selections;

  public PlayerSelectionManager(final MurderRun plugin) {
    this.plugin = plugin;
    this.selections = new HashMap<>();
  }

  public void addSelection(final Player player, final boolean killer) {
    final PlayerSelection selection = new PlayerSelection(this.plugin, player, killer);
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
