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
package me.brandonli.murderrun.game.player;

import java.util.HashSet;
import java.util.Set;
import me.brandonli.murderrun.game.Game;
import me.brandonli.murderrun.game.scheduler.GameScheduler;
import me.brandonli.murderrun.game.scheduler.reference.NullReference;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;

public final class LightManager {

  private final Game game;
  private final Set<GamePlayer> canSee;

  public LightManager(final Game game) {
    this.game = game;
    this.canSee = new HashSet<>();
  }

  public void startLightChecks() {
    final GameScheduler scheduler = this.game.getScheduler();
    final NullReference reference = NullReference.of();
    scheduler.scheduleRepeatedTask(this::checkSurvivorLight, 0, 1, reference);
  }

  private void checkSurvivorLight() {
    final GamePlayerManager manager = this.game.getPlayerManager();
    manager.applyToSurvivors(player -> {
      final Survivor survivor = (Survivor) player;
      if (survivor.canSee()) {
        if (!this.canSee.contains(survivor)) {
          survivor.removePotionEffect(PotionEffectType.BLINDNESS);
          this.canSee.add(survivor);
        }
      } else {
        if (survivor.isAlive()) {
          final PotionEffect effect = new PotionEffect(PotionEffectType.BLINDNESS, PotionEffect.INFINITE_DURATION, 1);
          survivor.addPotionEffects(effect);
        }
        this.canSee.remove(survivor);
      }
    });
  }
}
