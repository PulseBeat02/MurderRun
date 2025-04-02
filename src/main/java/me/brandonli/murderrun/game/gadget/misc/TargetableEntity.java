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
package me.brandonli.murderrun.game.gadget.misc;

import java.util.UUID;
import me.brandonli.murderrun.game.Game;
import me.brandonli.murderrun.game.player.GamePlayer;
import me.brandonli.murderrun.game.player.GamePlayerManager;
import org.bukkit.Location;
import org.bukkit.entity.Mob;
import org.bukkit.event.entity.EntityTargetEvent;

public interface TargetableEntity {
  default void handle(final EntityTargetEvent event, final String target, final Mob entity, final boolean killer) {
    final Game game = this.getGame();
    final UUID uuid = UUID.fromString(target);
    final GamePlayerManager manager = game.getPlayerManager();
    if (!manager.checkPlayerExists(uuid)) {
      entity.remove();
      return;
    }

    final Location location = entity.getLocation();
    final GamePlayer nearest;
    if (killer) {
      nearest = manager.getNearestKiller(location);
    } else {
      nearest = manager.getNearestLivingSurvivor(location);
    }

    if (nearest == null) {
      entity.remove();
      return;
    }

    event.setCancelled(true);

    nearest.apply(entity::setTarget);
  }

  Game getGame();
}
