package io.github.pulsebeat02.murderrun.map.event;

import io.github.pulsebeat02.murderrun.MurderRun;
import io.github.pulsebeat02.murderrun.game.MurderGame;
import io.github.pulsebeat02.murderrun.player.GamePlayer;
import io.github.pulsebeat02.murderrun.player.PlayerManager;
import io.github.pulsebeat02.murderrun.utils.PlayerUtils;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerQuitEvent;

import java.util.Optional;
import java.util.UUID;

public final class GamePlayerLeaveEvent implements Listener {

  private final MurderGame game;

  public GamePlayerLeaveEvent(final MurderGame game) {
    this.game = game;
  }

  @EventHandler(priority = EventPriority.LOWEST)
  private void onPlayerDisconnect(final PlayerQuitEvent event) {

    final PlayerManager manager = this.game.getPlayerManager();
    final Player player = event.getPlayer();
    final Optional<GamePlayer> optional = PlayerUtils.checkIfValidPlayer(this.game, player);
    if (optional.isEmpty()) {
      return;
    }

    player.setHealth(0f);

    final UUID uuid = player.getUniqueId();
    manager.removePlayer(uuid);
    manager.resetCachedPlayers();
  }
}
