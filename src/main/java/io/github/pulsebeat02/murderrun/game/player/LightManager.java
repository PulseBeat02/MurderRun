/*

MIT License

Copyright (c) 2025 Brandon Li

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
package io.github.pulsebeat02.murderrun.game.player;

import io.github.pulsebeat02.murderrun.game.Game;
import io.github.pulsebeat02.murderrun.game.scheduler.GameScheduler;
import io.github.pulsebeat02.murderrun.game.scheduler.reference.NullReference;
import java.util.HashSet;
import java.util.Set;
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
