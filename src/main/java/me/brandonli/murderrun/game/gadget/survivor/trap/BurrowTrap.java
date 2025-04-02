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
import me.brandonli.murderrun.game.player.Killer;
import me.brandonli.murderrun.game.scheduler.GameScheduler;
import me.brandonli.murderrun.game.scheduler.reference.StrictPlayerReference;
import me.brandonli.murderrun.locale.Message;
import me.brandonli.murderrun.utils.item.ItemFactory;
import org.bukkit.Location;
import org.bukkit.entity.Item;

public final class BurrowTrap extends SurvivorTrap {

  public BurrowTrap() {
    super(
      "burrow_trap",
      GameProperties.BURROW_COST,
      ItemFactory.createGadget("burrow_trap", GameProperties.BURROW_MATERIAL, Message.BURROW_NAME.build(), Message.BURROW_LORE.build()),
      Message.BURROW_ACTIVATE.build(),
      GameProperties.BURROW_COLOR
    );
  }

  @Override
  public void onTrapActivate(final Game game, final GamePlayer murderer, final Item item) {
    final Location location = murderer.getLocation();
    final Location clone = location.clone();
    clone.subtract(0, 30, 0);

    final GameScheduler scheduler = game.getScheduler();
    if (!(murderer instanceof final Killer killer)) {
      return;
    }

    final StrictPlayerReference reference = StrictPlayerReference.of(killer);
    final int duration = GameProperties.BURROW_DURATION;
    killer.disableJump(scheduler, duration);
    killer.disableWalkNoFOVEffects(scheduler, duration);
    killer.setForceMineBlocks(false);
    killer.teleport(clone);
    killer.setGravity(false);
    scheduler.scheduleTask(() -> this.resetState(killer, location), duration, reference);

    final GamePlayerManager manager = game.getPlayerManager();
    manager.playSoundForAllParticipants(GameProperties.BURROW_SOUND);
  }

  private void resetState(final Killer killer, final Location location) {
    killer.setGravity(true);
    killer.teleport(location);
    killer.setForceMineBlocks(true);
  }
}
