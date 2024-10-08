package io.github.pulsebeat02.murderrun.game.map.event;

import io.github.pulsebeat02.murderrun.game.Game;
import io.github.pulsebeat02.murderrun.game.player.GamePlayer;
import io.github.pulsebeat02.murderrun.game.player.PlayerManager;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.entity.EntityDismountEvent;

public final class GamePlayerDismountEvent extends GameEvent {

  public GamePlayerDismountEvent(final Game game) {
    super(game);
  }

  @EventHandler(priority = EventPriority.LOWEST)
  public void onEntityDismount(final EntityDismountEvent event) {
    final Entity entity = event.getEntity();
    if (!(entity instanceof final Player player)) {
      return;
    }

    final Game game = this.getGame();
    final PlayerManager manager = game.getPlayerManager();
    if (!manager.checkPlayerExists(player)) {
      return;
    }

    final GamePlayer gamePlayer = manager.getGamePlayer(player);
    if (!gamePlayer.canDismount()) {
      event.setCancelled(true);
    }
  }
}
