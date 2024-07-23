package io.github.pulsebeat02.murderrun.player;

import io.github.pulsebeat02.murderrun.game.MurderGame;
import org.bukkit.GameMode;
import org.bukkit.entity.Player;
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

  @Override
  public void onMatchStart() {
    super.onMatchStart();
    final Player player = this.getPlayer();
    player.setWalkSpeed(0.4f);
    player.setGameMode(GameMode.SURVIVAL);
  }
}
