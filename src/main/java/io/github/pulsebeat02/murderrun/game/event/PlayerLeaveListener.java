package io.github.pulsebeat02.murderrun.game.event;

import io.github.pulsebeat02.murderrun.game.lobby.PreGameManager;
import io.github.pulsebeat02.murderrun.game.lobby.PreGamePlayerManager;
import java.util.Collection;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerQuitEvent;

public final class PlayerLeaveListener implements Listener {

  private final PreGameManager manager;

  public PlayerLeaveListener(final PreGameManager manager) {
    this.manager = manager;
  }

  @EventHandler(priority = EventPriority.LOWEST)
  public void onPlayerQuit(final PlayerQuitEvent event) {
    final Player player = event.getPlayer();
    final PreGamePlayerManager playerManager = this.manager.getPlayerManager();
    final Collection<Player> participants = playerManager.getParticipants();
    if (!participants.contains(player)) {
      return;
    }

    playerManager.removeParticipantFromLobby(player);
  }
}
