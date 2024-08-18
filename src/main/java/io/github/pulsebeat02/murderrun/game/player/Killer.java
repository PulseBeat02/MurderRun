package io.github.pulsebeat02.murderrun.game.player;

import io.github.pulsebeat02.murderrun.game.Game;
import java.util.UUID;
import org.bukkit.event.entity.EntityPickupItemEvent;

public final class Killer extends GamePlayer {

  private boolean ignoreTraps;

  public Killer(final Game game, final UUID uuid) {
    super(game, uuid);
  }

  @Override
  public void onPlayerAttemptPickupPartEvent(final EntityPickupItemEvent event) {
    event.setCancelled(true);
  }

  public boolean isIgnoringTraps() {
    return ignoreTraps;
  }

  public void setIgnoreTraps(final boolean ignoreTraps) {
    this.ignoreTraps = ignoreTraps;
  }
}
