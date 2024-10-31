package io.github.pulsebeat02.murderrun.game.event;

import io.github.pulsebeat02.murderrun.game.lobby.PreGameManager;
import io.github.pulsebeat02.murderrun.game.lobby.PreGamePlayerManager;
import java.util.Collection;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.event.block.BlockPlaceEvent;

public final class BlockModifyListener implements Listener {

  private final PreGameManager manager;

  public BlockModifyListener(final PreGameManager manager) {
    this.manager = manager;
  }

  @EventHandler(priority = EventPriority.LOWEST)
  public void onBlockBreak(final BlockBreakEvent event) {
    final Player player = event.getPlayer();
    final PreGamePlayerManager playerManager = this.manager.getPlayerManager();
    final Collection<Player> participants = playerManager.getParticipants();
    if (!participants.contains(player)) {
      return;
    }
    event.setCancelled(true);
  }

  @EventHandler(priority = EventPriority.LOWEST)
  public void onBlockBreak(final BlockPlaceEvent event) {
    final Player player = event.getPlayer();
    final PreGamePlayerManager playerManager = this.manager.getPlayerManager();
    final Collection<Player> participants = playerManager.getParticipants();
    if (!participants.contains(player)) {
      return;
    }
    event.setCancelled(true);
  }
}
