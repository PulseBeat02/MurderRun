package io.github.pulsebeat02.murderrun.map.event;

import io.github.pulsebeat02.murderrun.game.MurderGame;
import io.github.pulsebeat02.murderrun.player.PlayerManager;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerQuitEvent;

import java.util.UUID;

public final class GamePlayerLeaveEvent implements Listener {

  private final MurderGame game;

  public GamePlayerLeaveEvent(final MurderGame game) {
    this.game = game;
  }

  @EventHandler
  private void onPlayerDisconnect(final PlayerQuitEvent event) {
    final PlayerManager manager = this.game.getPlayerManager();
    final Player player = event.getPlayer();
    final UUID uuid = player.getUniqueId();
    manager.removePlayer(uuid);
    manager.resetCachedPlayers();
  }
}
