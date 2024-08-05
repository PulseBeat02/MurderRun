package io.github.pulsebeat02.murderrun.game.map;

import io.github.pulsebeat02.murderrun.game.MurderGame;
import io.github.pulsebeat02.murderrun.game.MurderSettings;
import io.github.pulsebeat02.murderrun.game.arena.MurderArena;
import io.github.pulsebeat02.murderrun.utils.MapUtils;
import java.util.Collection;
import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;
import org.bukkit.util.BoundingBox;

public final class MurderMapResetManager {

  private final MurderMap map;

  public MurderMapResetManager(final MurderMap map) {
    this.map = map;
  }

  public void resetMap() {
    this.killExistingEntities();
    this.resetMapBlocksEntities();
  }

  private void killExistingEntities() {

    final MurderGame game = this.map.getGame();
    final MurderSettings settings = game.getSettings();
    final MurderArena arena = settings.getArena();
    final Location first = arena.getFirstCorner();
    final Location second = arena.getSecondCorner();
    final BoundingBox box = BoundingBox.of(first, second);
    final World world = first.getWorld();
    if (world == null) {
      throw new AssertionError("Location doesn't have World attached to it!");
    }

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

  public MurderMap getMap() {
    return this.map;
  }
}
