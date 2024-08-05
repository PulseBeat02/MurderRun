package io.github.pulsebeat02.murderrun.game.player;

import io.github.pulsebeat02.murderrun.game.MurderGame;
import java.util.UUID;
import org.bukkit.event.entity.EntityPickupItemEvent;

public final class Murderer extends GamePlayer {

  public Murderer(final MurderGame game, final UUID uuid) {
    super(game, uuid);
  }

  @Override
  public void onPlayerAttemptPickupPartEvent(final EntityPickupItemEvent event) {
    event.setCancelled(true);
  }
}
