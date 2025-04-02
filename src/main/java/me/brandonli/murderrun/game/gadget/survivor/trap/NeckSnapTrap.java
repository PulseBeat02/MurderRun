/*
 * This file is part of Murder Run, a spin-off game-mode of Dead by Daylight
 * Copyright (C) Brandon Li <https://brandonli.me/>
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program.  If not, see <https://www.gnu.org/licenses/>.
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
