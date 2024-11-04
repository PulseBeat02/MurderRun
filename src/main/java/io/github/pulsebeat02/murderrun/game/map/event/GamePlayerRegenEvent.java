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
