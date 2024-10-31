package io.github.pulsebeat02.murderrun.game.map.event;

import io.github.pulsebeat02.murderrun.game.Game;
import io.github.pulsebeat02.murderrun.game.GameStatus;
import io.github.pulsebeat02.murderrun.game.player.GamePlayer;
import io.github.pulsebeat02.murderrun.game.player.Killer;
import io.github.pulsebeat02.murderrun.game.player.PlayerManager;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.block.BlockBreakEvent;

public final class GamePlayerBlockBlackList extends GameEvent {

  public GamePlayerBlockBlackList(final Game game) {
    super(game);
  }

  @EventHandler(priority = EventPriority.LOWEST)
  public void onBlockBreak(final BlockBreakEvent event) {
    final Player player = event.getPlayer();
    final Game game = this.getGame();
    final PlayerManager manager = game.getPlayerManager();
    if (!manager.checkPlayerExists(player)) {
      return;
    }

    final GamePlayer gamePlayer = manager.getGamePlayer(player);
    if (!(gamePlayer instanceof Killer)) {
      return;
    }

    final GameStatus status = game.getStatus();
    if (status == GameStatus.SURVIVORS_RELEASED) {
      event.setCancelled(true);
    }
  }
}
