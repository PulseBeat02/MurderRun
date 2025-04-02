/*

MIT License

Copyright (c) 2024 Brandon Li

Permission is hereby granted, free of charge, to any person obtaining a copy
of this software and associated documentation files (the "Software"), to deal
in the Software without restriction, including without limitation the rights
to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
copies of the Software, and to permit persons to whom the Software is
furnished to do so, subject to the following conditions:

The above copyright notice and this permission notice shall be included in all
copies or substantial portions of the Software.

THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE
SOFTWARE.

*/
package io.github.pulsebeat02.murderrun.game.map;

import static java.util.Objects.requireNonNull;

import io.github.pulsebeat02.murderrun.game.Game;
import io.github.pulsebeat02.murderrun.utils.IOUtils;
import java.nio.file.Path;
import java.util.UUID;
import org.bukkit.Bukkit;
import org.bukkit.World;

public final class MapResetTool {

  private final GameMap map;

  public MapResetTool(final GameMap map) {
    this.map = map;
  }

  public void resetMap() {
    //    this.killExistingEntities();
    //    this.resetMapBlocksEntities();
    this.unloadWorld();
    this.deleteWorld();
  }

  private void deleteWorld() {
    final Game game = this.map.getGame();
    final UUID uuid = game.getGameUUID();
    final String name = uuid.toString();
    final Path path = IOUtils.getPluginDataFolderPath();
    final Path pluginParent = requireNonNull(path.getParent());
    final Path moreParent = requireNonNull(pluginParent.getParent());
    final Path world = moreParent.resolve(name);
    IOUtils.deleteExistingDirectory(world);
  }

  private void unloadWorld() {
    final Game game = this.map.getGame();
    final UUID uuid = game.getGameUUID();
    final String name = uuid.toString();
    final World world = requireNonNull(Bukkit.getWorld(name));
    Bukkit.unloadWorld(world, false);
  }

  //  private void killExistingEntities() {
  //    final Game game = this.map.getGame();
  //    final GameSettings settings = game.getSettings();
  //    final Arena arena = requireNonNull(settings.getArena());
  //    final Location first = arena.getFirstCorner();
  //    final Location second = arena.getSecondCorner();
  //    final BoundingBox box = BoundingBox.of(first, second);
  //    final World world = requireNonNull(first.getWorld());
  //    final Collection<Entity> entities = world.getNearbyEntities(box);
  //    for (final Entity entity : entities) {
  //      if (entity instanceof Player || entity instanceof Display) {
  //        continue;
  //      }
  //      entity.remove();
  //    }
  //  }
  //
  //  private void resetMapBlocksEntities() {
  //    final MapSchematicIO io = this.map.getMapSchematicIO();
  //    io.pasteMap();
  //  }

  public GameMap getMap() {
    return this.map;
  }
}
