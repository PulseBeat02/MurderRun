package io.github.pulsebeat02.murderrun.map;

import io.github.pulsebeat02.murderrun.arena.MurderArena;
import io.github.pulsebeat02.murderrun.game.GameSettings;
import io.github.pulsebeat02.murderrun.game.MurderGame;
import io.github.pulsebeat02.murderrun.utils.MapUtils;
import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;
import org.bukkit.util.BoundingBox;

import java.util.Collection;

public final class MapResetManager {

  private final MurderMap map;

  public MapResetManager(final MurderMap map) {
    this.map = map;
  }

  public void resetMap() {
    this.killExistingEntities();
    this.resetMapBlocksEntities();
  }

  private void killExistingEntities() {
    final MurderGame game = this.map.getGame();
    final GameSettings settings = game.getSettings();
    final MurderArena arena = settings.getArena();
    final Location first = arena.getFirstCorner();
    final Location second = arena.getSecondCorner();
    final BoundingBox box = BoundingBox.of(first, second);
    final World world = first.getWorld();
    final Collection<Entity> entities = world.getNearbyEntities(box);
    for (final Entity entity : entities) {
      if (entity instanceof Player) {
        continue;
      }
      entity.remove();
    }
  }

  private void resetMapBlocksEntities() {
    MapUtils.resetMap(this.map);
  }
}
