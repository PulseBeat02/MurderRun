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
package me.brandonli.murderrun.game.ability.killer;

import static java.util.Objects.requireNonNull;

import me.brandonli.murderrun.game.Game;
import me.brandonli.murderrun.game.GameProperties;
import me.brandonli.murderrun.game.player.GamePlayerManager;
import me.brandonli.murderrun.locale.Message;
import me.brandonli.murderrun.utils.item.ItemFactory;
import org.bukkit.attribute.Attribute;
import org.bukkit.attribute.AttributeInstance;

public final class Reach extends KillerAbility {

  public static final String REACH_NAME = "reach";

  public Reach(final Game game) {
    super(
        game,
        REACH_NAME,
        ItemFactory.createAbility(
            REACH_NAME, Message.REACH_NAME.build(), Message.REACH_LORE.build(), 1));
  }

  @Override
  public void start() {
    final Game game = this.getGame();
    final GamePlayerManager playerManager = game.getPlayerManager();
    final GameProperties properties = game.getProperties();
    final double reach = properties.getReachDistance();
    playerManager.applyToLivingKillers(participant -> {
      if (!participant.hasAbility(REACH_NAME)) {
        return;
      }
      if (this.invokeEvent(participant)) {
        return;
      }
      final AttributeInstance instance =
          requireNonNull(participant.getAttribute(Attribute.BLOCK_INTERACTION_RANGE));
      instance.setBaseValue(reach);
    });
  }

  @Override
  public void shutdown() {
    final Game game = this.getGame();
    final GamePlayerManager playerManager = game.getPlayerManager();
    playerManager.applyToLivingKillers(participant -> {
      if (!participant.hasAbility(REACH_NAME)) {
        return;
      }
      participant.resetAttribute(Attribute.BLOCK_INTERACTION_RANGE);
    });
  }
}
