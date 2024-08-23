package io.github.pulsebeat02.murderrun.game.player;

import io.github.pulsebeat02.murderrun.game.Game;
import java.util.UUID;

public final class Killer extends GamePlayer {

  private boolean ignoreTraps;
  private boolean forceMine;

  public Killer(final Game game, final UUID uuid) {
    super(game, uuid);
    this.forceMine = true;
  }

  public boolean isIgnoringTraps() {
    return this.ignoreTraps;
  }

  public void setIgnoreTraps(final boolean ignoreTraps) {
    this.ignoreTraps = ignoreTraps;
  }

  public void setForceMineBlocks(final boolean mineBlocks) {
    this.forceMine = mineBlocks;
  }

  public boolean canForceMineBlocks() {
    return this.forceMine;
  }
}
