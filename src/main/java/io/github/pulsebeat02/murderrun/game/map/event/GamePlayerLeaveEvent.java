package io.github.pulsebeat02.murderrun.game.map.event;

import io.github.pulsebeat02.murderrun.game.Game;
import io.github.pulsebeat02.murderrun.game.player.PlayerManager;
import java.util.UUID;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.player.PlayerQuitEvent;

public final class GamePlayerLeaveEvent extends GameEvent {

  public GamePlayerLeaveEvent(final Game game) {
    super(game);
  }

  @EventHandler(priority = EventPriority.LOWEST)
  private void onPlayerDisconnect(final PlayerQuitEvent event) {

    final Player player = event.getPlayer();
    if (!this.isGamePlayer(player)) {
      return;
    }

    player.setHealth(0f);

    final Game game = this.getGame();
    final PlayerManager manager = game.getPlayerManager();
    final UUID uuid = player.getUniqueId();
    manager.removePlayer(uuid);
  }
}
