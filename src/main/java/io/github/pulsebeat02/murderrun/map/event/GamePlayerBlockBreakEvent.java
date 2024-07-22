package io.github.pulsebeat02.murderrun.map.event;

import io.github.pulsebeat02.murderrun.game.MurderGame;
import io.github.pulsebeat02.murderrun.player.GamePlayer;
import io.github.pulsebeat02.murderrun.player.Murderer;
import io.github.pulsebeat02.murderrun.player.PlayerManager;
import org.bukkit.Location;
import org.bukkit.SoundCategory;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.BlockBreakEvent;

import java.util.Optional;
import java.util.UUID;

public final class GamePlayerBlockBreakEvent implements Listener {

  private final MurderGame game;

  public GamePlayerBlockBreakEvent(final MurderGame game) {
    this.game = game;
  }

  @EventHandler
  private void onBlockBreakEvent(final BlockBreakEvent event) {

    final Player player = event.getPlayer();
    final UUID uuid = player.getUniqueId();
    final PlayerManager manager = this.game.getPlayerManager();
    final Optional<GamePlayer> optional = manager.lookupPlayer(uuid);
    if (optional.isEmpty()) {
      return;
    }

    final GamePlayer murderer = optional.get();
    final Location murdererLocation = player.getLocation();
    if (murderer instanceof Murderer) {
      final String key = "murder_run:chainsaw";
      for (final GamePlayer gamePlayer : manager.getParticipants()) {
        final Player pl = gamePlayer.getPlayer();
        pl.playSound(murdererLocation, key, SoundCategory.AMBIENT, 1, 1);
      }
    }
  }
}
