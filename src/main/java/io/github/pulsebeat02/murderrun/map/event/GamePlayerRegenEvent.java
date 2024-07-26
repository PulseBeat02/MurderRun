package io.github.pulsebeat02.murderrun.map.event;

import io.github.pulsebeat02.murderrun.game.MurderGame;
import io.github.pulsebeat02.murderrun.player.GamePlayer;
import io.github.pulsebeat02.murderrun.utils.PlayerUtils;
import java.util.Optional;
import java.util.Set;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityRegainHealthEvent;

public final class GamePlayerRegenEvent implements Listener {

  private static final Set<EntityRegainHealthEvent.RegainReason> REASONS =
      Set.of(
          EntityRegainHealthEvent.RegainReason.SATIATED,
          EntityRegainHealthEvent.RegainReason.REGEN,
          EntityRegainHealthEvent.RegainReason.EATING);

  private final MurderGame game;

  public GamePlayerRegenEvent(final MurderGame game) {
    this.game = game;
  }

  public MurderGame getGame() {
    return this.game;
  }

  @EventHandler(priority = EventPriority.LOWEST)
  private void onPlayerRegenEvent(final EntityRegainHealthEvent event) {

    final Entity entity = event.getEntity();
    if (!(entity instanceof final Player player)) {
      return;
    }

    final Optional<GamePlayer> optional = PlayerUtils.checkIfValidPlayer(this.game, player);
    if (optional.isEmpty()) {
      return;
    }

    final EntityRegainHealthEvent.RegainReason reason = event.getRegainReason();
    if (REASONS.contains(reason)) {
      event.setCancelled(true);
    }
  }
}
