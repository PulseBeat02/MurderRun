package io.github.pulsebeat02.murderrun.player;

import java.util.UUID;

public final class InnocentPlayer extends GamePlayer {

  private boolean carPart;

  public InnocentPlayer(final UUID uuid) {
    super(uuid);
  }

  public boolean hasCarPart() {
    return this.carPart;
  }

  public void setHasCarPart(final boolean hasCarPart) {
    this.carPart = hasCarPart;
  }
}
