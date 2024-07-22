package io.github.pulsebeat02.murderrun.map.event;

import io.github.pulsebeat02.murderrun.MurderGame;
import io.github.pulsebeat02.murderrun.player.GamePlayer;
import io.github.pulsebeat02.murderrun.player.PlayerManager;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.PlayerDeathEvent;

import java.util.Optional;
import java.util.UUID;

public final class GamePlayerDeathEvent implements Listener {

  private final MurderGame game;

  public GamePlayerDeathEvent(final MurderGame game) {
    this.game = game;
  }

  @EventHandler
  public void onPlayerDeath(final PlayerDeathEvent event) {
    final Player player = event.getEntity();
    final UUID uuid = player.getUniqueId();
    final PlayerManager manager = this.game.getPlayerManager();
    final Optional<GamePlayer> optional = manager.lookupPlayer(uuid);
    if (optional.isEmpty()) {
      return;
    }
    final GamePlayer gamePlayer = optional.get();
    gamePlayer.markDeath();
  }
}
