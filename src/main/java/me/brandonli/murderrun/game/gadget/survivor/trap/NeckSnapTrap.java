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
package me.brandonli.murderrun.game.gadget.survivor.trap;

import me.brandonli.murderrun.game.Game;
import me.brandonli.murderrun.game.GameProperties;
import me.brandonli.murderrun.game.player.GamePlayer;
import me.brandonli.murderrun.game.player.GamePlayerManager;
import me.brandonli.murderrun.game.scheduler.GameScheduler;
import me.brandonli.murderrun.game.scheduler.reference.StrictPlayerReference;
import me.brandonli.murderrun.locale.Message;
import me.brandonli.murderrun.utils.item.ItemFactory;
import org.bukkit.Location;
import org.bukkit.entity.Item;
import org.bukkit.util.Vector;

public final class NeckSnapTrap extends SurvivorTrap {

  private static final Vector UP = new Vector(0, 1, 0);

  public NeckSnapTrap() {
    super(
      "neck_snap_trap",
      GameProperties.NECK_SNAP_COST,
      ItemFactory.createGadget(
        "neck_snap_trap",
        GameProperties.NECK_SNAP_MATERIAL,
        Message.NECK_SNAP_NAME.build(),
        Message.NECK_SNAP_LORE.build()
      ),
      Message.NECK_SNAP_ACTIVATE.build(),
      GameProperties.NECK_SNAP_COLOR
    );
  }

  @Override
  public void onTrapActivate(final Game game, final GamePlayer murderer, final Item item) {
    final GamePlayerManager manager = game.getPlayerManager();
    final GameScheduler scheduler = game.getScheduler();
    final StrictPlayerReference reference = StrictPlayerReference.of(murderer);
    scheduler.scheduleRepeatedTask(() -> this.setLookDirection(murderer), 0, 5, GameProperties.NECK_SNAP_DURATION, reference);
    manager.playSoundForAllParticipants(GameProperties.NECK_SNAP_SOUND);
  }

  private void setLookDirection(final GamePlayer player) {
    final Location location = player.getLocation();
    location.setDirection(UP);
    player.teleport(location);
  }
}
