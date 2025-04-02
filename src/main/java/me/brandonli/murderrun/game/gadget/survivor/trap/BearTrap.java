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
import me.brandonli.murderrun.locale.Message;
import me.brandonli.murderrun.utils.item.ItemFactory;
import org.bukkit.entity.Item;

public final class BearTrap extends SurvivorTrap {

  public BearTrap() {
    super(
      "bear_trap",
      GameProperties.BEAR_COST,
      ItemFactory.createGadget("bear_trap", GameProperties.BEAR_MATERIAL, Message.BEAR_NAME.build(), Message.BEAR_LORE.build()),
      Message.BEAR_ACTIVATE.build(),
      GameProperties.BEAR_COLOR
    );
  }

  @Override
  public void onTrapActivate(final Game game, final GamePlayer murderer, final Item item) {
    final GameScheduler scheduler = game.getScheduler();
    final int duration = GameProperties.BEAR_DURATION;
    murderer.disableJump(scheduler, duration);
    murderer.disableWalkWithFOVEffects(duration);

    final GamePlayerManager manager = game.getPlayerManager();
    manager.playSoundForAllParticipants(GameProperties.BEAR_SOUND);
  }
}
