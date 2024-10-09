package io.github.pulsebeat02.murderrun.commmand;

import io.github.pulsebeat02.murderrun.game.Game;
import io.github.pulsebeat02.murderrun.game.GameResult;
import java.util.Collection;
import java.util.HashSet;

public final class GameShutdownManager {

  private final Collection<Game> currentGames;

  public GameShutdownManager() {
    this.currentGames = new HashSet<>();
  }

  public void forceShutdown() {
    for (final Game game : this.currentGames) {
      game.finishGame(GameResult.INTERRUPTED);
    }
  }

  public void addGame(final Game game) {
    this.currentGames.add(game);
  }

  public void removeGame(final Game game) {
    this.currentGames.remove(game);
  }
}
