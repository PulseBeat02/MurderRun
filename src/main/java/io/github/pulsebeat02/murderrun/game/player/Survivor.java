package io.github.pulsebeat02.murderrun.game.player;

import io.github.pulsebeat02.murderrun.game.Game;
import java.util.UUID;

public final class Survivor extends GamePlayer {

  private boolean canPickupCarPart;
  private boolean canPlaceBlocks;
  private boolean carPart;

  public Survivor(final Game game, final UUID uuid) {
    super(game, uuid);
    this.canPickupCarPart = true;
    this.canPlaceBlocks = false;
  }

  public void setHasCarPart(final boolean hasCarPart) {
    this.carPart = hasCarPart;
  }

  public boolean hasCarPart() {
    return this.carPart;
  }

  public void setCanPickupCarPart(final boolean canPickupCarPart) {
    this.canPickupCarPart = canPickupCarPart;
  }

  public boolean canPickupCarPart() {
    return this.canPickupCarPart;
  }

  public boolean canPlaceBlocks() {
    return this.canPlaceBlocks;
  }

  public void setCanPlaceBlocks(final boolean canPlaceBlocks) {
    this.canPlaceBlocks = canPlaceBlocks;
  }
}
