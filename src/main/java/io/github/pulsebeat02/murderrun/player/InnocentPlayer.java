package io.github.pulsebeat02.murderrun.player;

import io.github.pulsebeat02.murderrun.MurderGame;

import java.util.UUID;

public final class InnocentPlayer extends GamePlayer {

  private boolean carPart;

  public InnocentPlayer(final MurderGame game, final UUID uuid) {
    super(game, uuid);
  }

  public boolean hasCarPart() {
    return this.carPart;
  }

  public void setHasCarPart(final boolean hasCarPart) {
    this.carPart = hasCarPart;
  }
}
