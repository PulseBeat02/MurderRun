/*

MIT License

Copyright (c) 2024 Brandon Li

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
package io.github.pulsebeat02.murderrun.game.lobby;

import fr.mrmicky.fastboard.adventure.FastBoard;
import java.util.Collection;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;
import java.util.function.Consumer;
import net.kyori.adventure.text.Component;
import org.bukkit.entity.Player;

public final class LobbySidebarManager {

  private final Map<UUID, FastBoard> boards;

  public LobbySidebarManager() {
    this.boards = new HashMap<>();
  }

  public void addPlayer(final Player player) {
    final UUID uuid = player.getUniqueId();
    final FastBoard board = new FastBoard(player);
    this.boards.put(uuid, board);
  }

  public void updateLine(final int index, final Component line) {
    this.handleScoreboardUpdate(consumer -> consumer.updateLine(index, line));
  }

  public void updateLines(final Component... lines) {
    this.handleScoreboardUpdate(consumer -> consumer.updateLines(lines));
  }

  public void updateTitle(final Component title) {
    this.handleScoreboardUpdate(consumer -> consumer.updateTitle(title));
  }

  public void delete() {
    this.handleScoreboardUpdate(FastBoard::delete);
  }

  private void handleScoreboardUpdate(final Consumer<FastBoard> consumer) {
    final Collection<FastBoard> boards = this.boards.values();
    for (final FastBoard board : boards) {
      if (board.isDeleted()) {
        continue;
      }
      consumer.accept(board);
    }
  }
}
