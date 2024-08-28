package io.github.pulsebeat02.murderrun.game.map.event;

import io.github.pulsebeat02.murderrun.game.Game;
import io.github.pulsebeat02.murderrun.game.player.GamePlayer;
import io.github.pulsebeat02.murderrun.game.player.PlayerManager;
import org.bukkit.GameMode;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.player.PlayerTeleportEvent;
import org.bukkit.event.player.PlayerTeleportEvent.TeleportCause;

public final class GamePlayerTeleportEvent extends GameEvent {

  public GamePlayerTeleportEvent(final Game game) {
    super(game);
  }

  @EventHandler
  public void onPlayerTeleportEvent(final PlayerTeleportEvent event) {

    final Player player = event.getPlayer();
    if (!this.isGamePlayer(player)) {
      return;
    }

    final GameMode mode = player.getGameMode();
    if (mode != GameMode.SPECTATOR) {
      return;
    }

    final TeleportCause cause = event.getCause();
    if (cause != TeleportCause.SPECTATE) {
      return;
    }

    final Game game = this.getGame();
    final PlayerManager manager = game.getPlayerManager();
    final GamePlayer gamePlayer = manager.getGamePlayer(player);
    if (gamePlayer.canSpectatorTeleport()) {
      return;
    }

    event.setCancelled(true);
  }
}
