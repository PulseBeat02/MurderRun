package io.github.pulsebeat02.murderrun.game.gadget.misc;

import io.github.pulsebeat02.murderrun.game.Game;
import io.github.pulsebeat02.murderrun.game.player.GamePlayer;
import io.github.pulsebeat02.murderrun.game.player.PlayerManager;
import java.util.UUID;
import org.bukkit.Location;
import org.bukkit.entity.Mob;
import org.bukkit.entity.Player;
import org.bukkit.event.entity.EntityTargetEvent;

public interface TargetableEntity {

  default void handle(
      final EntityTargetEvent event, final String target, final Mob entity, final boolean killer) {

    final Game game = this.getGame();
    final UUID uuid = UUID.fromString(target);
    final PlayerManager manager = game.getPlayerManager();
    if (!manager.checkPlayerExists(uuid)) {
      entity.remove();
      return;
    }

    final Location location = entity.getLocation();
    final GamePlayer nearest;
    if (killer) {
      nearest = manager.getNearestKiller(location);
    } else {
      nearest = manager.getNearestSurvivor(location);
    }

    if (nearest == null) {
      entity.remove();
      return;
    }

    event.setCancelled(true);

    final Player internal = nearest.getInternalPlayer();
    entity.setTarget(internal);
  }

  Game getGame();
}
