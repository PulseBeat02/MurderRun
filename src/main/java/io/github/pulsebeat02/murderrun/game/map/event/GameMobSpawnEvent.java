package io.github.pulsebeat02.murderrun.game.map.event;

import static java.util.Objects.requireNonNull;

import io.github.pulsebeat02.murderrun.game.Game;
import io.github.pulsebeat02.murderrun.game.GameSettings;
import io.github.pulsebeat02.murderrun.game.arena.Arena;
import org.bukkit.Location;
import org.bukkit.entity.LivingEntity;
import org.bukkit.event.EventHandler;
import org.bukkit.event.entity.CreatureSpawnEvent;
import org.bukkit.event.entity.CreatureSpawnEvent.SpawnReason;
import org.bukkit.util.BoundingBox;
import org.bukkit.util.Vector;

public final class GameMobSpawnEvent extends GameEvent {

  public GameMobSpawnEvent(final Game game) {
    super(game);
  }

  @EventHandler
  public void onEntitySpawn(final CreatureSpawnEvent event) {

    final Game game = this.getGame();
    final GameSettings settings = game.getSettings();
    final Arena arena = requireNonNull(settings.getArena());
    final BoundingBox boundingBox = arena.createBox();
    final LivingEntity entity = event.getEntity();
    final Location location = entity.getLocation();
    final Vector vector = location.toVector();
    if (!boundingBox.contains(vector)) {
      return;
    }

    final SpawnReason reason = event.getSpawnReason();
    if (reason == SpawnReason.NATURAL) {
      event.setCancelled(true);
    }
  }
}
