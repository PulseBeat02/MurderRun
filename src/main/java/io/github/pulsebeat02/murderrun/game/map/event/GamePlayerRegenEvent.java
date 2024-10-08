package io.github.pulsebeat02.murderrun.game.map.event;

import io.github.pulsebeat02.murderrun.game.Game;
import java.util.Set;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.entity.EntityRegainHealthEvent;

public final class GamePlayerRegenEvent extends GameEvent {

  private static final Set<EntityRegainHealthEvent.RegainReason> REASONS = Set.of(
    EntityRegainHealthEvent.RegainReason.SATIATED,
    EntityRegainHealthEvent.RegainReason.REGEN,
    EntityRegainHealthEvent.RegainReason.EATING
  );

  public GamePlayerRegenEvent(final Game game) {
    super(game);
  }

  @EventHandler(priority = EventPriority.LOWEST)
  private void onPlayerRegenEvent(final EntityRegainHealthEvent event) {
    final Entity entity = event.getEntity();
    if (!(entity instanceof final Player player)) {
      return;
    }

    if (!this.isGamePlayer(player)) {
      return;
    }

    final EntityRegainHealthEvent.RegainReason reason = event.getRegainReason();
    if (REASONS.contains(reason)) {
      event.setCancelled(true);
    }
  }
}
