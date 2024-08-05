package io.github.pulsebeat02.murderrun.game.player;

import io.github.pulsebeat02.murderrun.game.MurderGame;
import java.util.UUID;
import org.bukkit.event.entity.EntityPickupItemEvent;

public final class Innocent extends GamePlayer {

  private boolean carPart;

  public Innocent(final MurderGame game, final UUID uuid) {
    super(game, uuid);
  }

  @Override
  public void onPlayerAttemptPickupPartEvent(final EntityPickupItemEvent event) {
    this.setHasCarPart(true);
  }

  public void setHasCarPart(final boolean hasCarPart) {
    this.carPart = hasCarPart;
  }

  public boolean hasCarPart() {
    return this.carPart;
  }
}
