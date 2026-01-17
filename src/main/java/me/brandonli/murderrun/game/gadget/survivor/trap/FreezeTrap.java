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

public final class FreezeTrap extends SurvivorTrap {

  public FreezeTrap(final Game game) {
    final GameProperties properties = game.getProperties();
    super(
        "freeze_trap",
        properties.getFreezeCost(),
        ItemFactory.createGadget(
            "freeze_trap",
            properties.getFreezeMaterial(),
            Message.FREEZE_NAME.build(),
            Message.FREEZE_LORE.build()),
        Message.FREEZE_ACTIVATE.build(),
        properties.getFreezeColor());
  }

  @Override
  public void onTrapActivate(final Game game, final GamePlayer murderer, final Item item) {
    final GameScheduler scheduler = game.getScheduler();
    final GameProperties properties = game.getProperties();
    final int duration = properties.getFreezeEffectDuration();
    murderer.setFreezeTicks(duration);
    murderer.disableJump(scheduler, duration);
    murderer.setFreezeTicks(0);
    murderer.disableWalkWithFOVEffects(properties.getFreezeDuration());

    final GamePlayerManager manager = game.getPlayerManager();
    manager.playSoundForAllParticipants(properties.getFreezeSound());
  }
}
