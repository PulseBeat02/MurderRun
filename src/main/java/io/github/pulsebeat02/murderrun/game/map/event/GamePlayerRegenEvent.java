package io.github.pulsebeat02.murderrun.game.map.event;

import io.github.pulsebeat02.murderrun.game.Game;
import io.github.pulsebeat02.murderrun.game.player.PlayerManager;
import java.util.Set;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityRegainHealthEvent;

public final class GamePlayerRegenEvent implements Listener {

  private static final Set<EntityRegainHealthEvent.RegainReason> REASONS = Set.of(
      EntityRegainHealthEvent.RegainReason.SATIATED,
      EntityRegainHealthEvent.RegainReason.REGEN,
      EntityRegainHealthEvent.RegainReason.EATING);

  private final Game game;

  public GamePlayerRegenEvent(final Game game) {
    this.game = game;
  }

  public Game getGame() {
    return this.game;
  }

  @EventHandler(priority = EventPriority.LOWEST)
  private void onPlayerRegenEvent(final EntityRegainHealthEvent event) {

    final Entity entity = event.getEntity();
    if (!(entity instanceof final Player player)) {
      return;
    }

    final PlayerManager manager = this.game.getPlayerManager();
    final boolean valid = manager.checkPlayerExists(player);
    if (!valid) {
      return;
    }

    final EntityRegainHealthEvent.RegainReason reason = event.getRegainReason();
    if (REASONS.contains(reason)) {
      event.setCancelled(true);
    }
  }
}
