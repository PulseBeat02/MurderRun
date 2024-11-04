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
package io.github.pulsebeat02.murderrun.game.gadget.survivor.trap;

import io.github.pulsebeat02.murderrun.game.Game;
import io.github.pulsebeat02.murderrun.game.GameProperties;
import io.github.pulsebeat02.murderrun.game.player.GamePlayer;
import io.github.pulsebeat02.murderrun.game.player.Killer;
import io.github.pulsebeat02.murderrun.game.player.PlayerManager;
import io.github.pulsebeat02.murderrun.game.scheduler.GameScheduler;
import io.github.pulsebeat02.murderrun.locale.Message;
import java.awt.Color;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.entity.Item;

public final class BurrowTrap extends SurvivorTrap {

  public BurrowTrap() {
    super(
      "burrow",
      Material.DIRT,
      Message.BURROW_NAME.build(),
      Message.BURROW_LORE.build(),
      Message.BURROW_ACTIVATE.build(),
      GameProperties.BURROW_COST,
      new Color(49, 42, 41)
    );
  }

  @Override
  public void onTrapActivate(final Game game, final GamePlayer murderer, final Item item) {
    final Location location = murderer.getLocation();
    final Location clone = location.clone();
    clone.subtract(0, 50, 0);

    final GameScheduler scheduler = game.getScheduler();
    if (!(murderer instanceof final Killer killer)) {
      return;
    }

    final int duration = GameProperties.BURROW_DURATION;
    killer.disableJump(scheduler, duration);
    killer.disableWalkNoFOVEffects(scheduler, duration);
    killer.setForceMineBlocks(false);
    killer.teleport(clone);
    killer.setGravity(true);
    scheduler.scheduleTask(() -> this.resetState(killer, location), duration);

    final PlayerManager manager = game.getPlayerManager();
    manager.playSoundForAllParticipants(GameProperties.BURROW_SOUND);
  }

  private void resetState(final Killer killer, final Location location) {
    killer.teleport(location);
    killer.setForceMineBlocks(true);
  }
}
