package io.github.pulsebeat02.murderrun.game;

@FunctionalInterface
public interface GameEndCallback {
  void onGameFinish(final Game game, final GameResult result);
}
