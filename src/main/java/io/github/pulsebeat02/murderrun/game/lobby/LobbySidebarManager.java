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
