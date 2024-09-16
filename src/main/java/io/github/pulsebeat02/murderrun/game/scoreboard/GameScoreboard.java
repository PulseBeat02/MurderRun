package io.github.pulsebeat02.murderrun.game.scoreboard;

import io.github.pulsebeat02.murderrun.MurderRun;
import io.github.pulsebeat02.murderrun.game.Game;
import net.megavex.scoreboardlibrary.api.ScoreboardLibrary;
import net.megavex.scoreboardlibrary.api.sidebar.Sidebar;

public final class GameScoreboard {

  private final Game game;

  public GameScoreboard(final Game game) {
    this.game = game;
  }

  private Sidebar createSidebar() {
    final MurderRun plugin = this.game.getPlugin();
    final ScoreboardLibrary library = plugin.getScoreboardLibrary();
    final Sidebar sidebar = library.createSidebar();
    return sidebar;
  }
}
