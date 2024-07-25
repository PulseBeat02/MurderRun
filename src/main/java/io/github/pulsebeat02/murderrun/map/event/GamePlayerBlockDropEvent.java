package io.github.pulsebeat02.murderrun.map.event;

import io.github.pulsebeat02.murderrun.game.MurderGame;
import io.github.pulsebeat02.murderrun.player.GamePlayer;
import io.github.pulsebeat02.murderrun.utils.PlayerUtils;
import java.util.Optional;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.block.BlockDropItemEvent;

public final class GamePlayerBlockDropEvent implements Listener {

  private final MurderGame game;

  public GamePlayerBlockDropEvent(final MurderGame game) {
    this.game = game;
  }

  public MurderGame getGame() {
    return this.game;
  }

  @EventHandler(priority = EventPriority.LOWEST)
  private void onBlockDropEvent(final BlockDropItemEvent event) {

    final Player player = event.getPlayer();
    final Optional<GamePlayer> optional = PlayerUtils.checkIfValidPlayer(this.game, player);
    if (optional.isEmpty()) {
      return;
    }

    event.setCancelled(true);
  }
}
