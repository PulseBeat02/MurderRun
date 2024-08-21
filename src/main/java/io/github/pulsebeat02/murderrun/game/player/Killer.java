package io.github.pulsebeat02.murderrun.game.player;

import io.github.pulsebeat02.murderrun.game.Game;
import java.util.UUID;

public final class Killer extends GamePlayer {

  private boolean ignoreTraps;

  public Killer(final Game game, final UUID uuid) {
    super(game, uuid);
  }

  public boolean isIgnoringTraps() {
    return this.ignoreTraps;
  }

  public void setIgnoreTraps(final boolean ignoreTraps) {
    this.ignoreTraps = ignoreTraps;
  }
}
