package io.github.pulsebeat02.murderrun.game.event;

import io.github.pulsebeat02.murderrun.game.GameManager;
import java.util.Collection;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityDamageEvent;

public final class DamagePreventionListener implements Listener {

  private final GameManager manager;

  public DamagePreventionListener(final GameManager manager) {
    this.manager = manager;
  }

  @EventHandler(priority = EventPriority.LOWEST)
  public void onDamage(final EntityDamageEvent event) {

    final Entity entity = event.getEntity();
    if (!(entity instanceof Player player)) {
      return;
    }

    final Collection<Player> participants = this.manager.getParticipants();
    if (!participants.contains(player)) {
      return;
    }

    event.setCancelled(true);
  }
}
