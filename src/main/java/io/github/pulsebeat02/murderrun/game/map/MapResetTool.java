package io.github.pulsebeat02.murderrun.game.map;

import static java.util.Objects.requireNonNull;

import io.github.pulsebeat02.murderrun.game.Game;
import io.github.pulsebeat02.murderrun.game.GameSettings;
import io.github.pulsebeat02.murderrun.game.arena.Arena;
import java.util.Collection;
import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;
import org.bukkit.util.BoundingBox;

public final class MapResetTool {

  private final Map map;

  public MapResetTool(final Map map) {
    this.map = map;
  }

  public void resetMap() {
    this.killExistingEntities();
    this.resetMapBlocksEntities();
  }

  private void killExistingEntities() {
    final Game game = this.map.getGame();
    final GameSettings settings = game.getSettings();
    final Arena arena = requireNonNull(settings.getArena());
    final Location first = arena.getFirstCorner();
    final Location second = arena.getSecondCorner();
    final BoundingBox box = BoundingBox.of(first, second);
    final World world = requireNonNull(first.getWorld());
    final Collection<Entity> entities = world.getNearbyEntities(box);
    for (final Entity entity : entities) {
      if (entity instanceof Player) {
        continue;
      }
      entity.remove();
    }
  }

  private void resetMapBlocksEntities() {
    final MapSchematicIO io = this.map.getMapSchematicIO();
    io.resetMap();
  }

  public Map getMap() {
    return this.map;
  }
}
