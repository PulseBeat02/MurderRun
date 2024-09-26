package io.github.pulsebeat02.murderrun.game;

public interface GameEventsListener {

  void onGameFinish(final Game game, final GameResult result);

  void onGameStart(final Game game);
}
