package io.github.pulsebeat02.murderrun.player;

import io.github.pulsebeat02.murderrun.MurderGame;
import org.bukkit.event.player.PlayerAttemptPickupItemEvent;

import java.util.UUID;

public final class Murderer extends GamePlayer {
  public Murderer(final MurderGame game, final UUID uuid) {
    super(game, uuid);
  }

  @Override
  public void onPlayerAttemptPickupPartEvent(final PlayerAttemptPickupItemEvent event) {
    event.setCancelled(true);
  }
}
