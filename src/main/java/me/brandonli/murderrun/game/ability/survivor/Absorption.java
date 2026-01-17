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
package me.brandonli.murderrun.game.ability.survivor;

import me.brandonli.murderrun.game.Game;
import me.brandonli.murderrun.game.GameProperties;
import me.brandonli.murderrun.game.player.GamePlayerManager;
import me.brandonli.murderrun.locale.Message;
import me.brandonli.murderrun.utils.item.ItemFactory;
import org.bukkit.inventory.PlayerInventory;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;

public final class Absorption extends SurvivorAbility {

  public Absorption(final Game game) {
    super(
        game,
        "absorption",
        ItemFactory.createAbility(
            "absorption", Message.ABSORPTION_NAME.build(), Message.ABSORPTION_LORE.build(), 1));
  }

  @Override
  public void start() {
    final Game game = this.getGame();
    final GamePlayerManager manager = game.getPlayerManager();
    final GameProperties properties = game.getProperties();
    final int level = properties.getAbsorptionLevel();
    manager.applyToAllParticipants(participant -> {
      final PlayerInventory inventory = participant.getInventory();
      if (!participant.hasAbility("absorption")) {
        return;
      }
      if (this.invokeEvent(participant)) {
        return;
      }
      participant.addPotionEffects(
          PotionEffectType.ABSORPTION.createEffect(PotionEffect.INFINITE_DURATION, level));
    });
  }
}
