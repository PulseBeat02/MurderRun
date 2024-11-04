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
import io.github.pulsebeat02.murderrun.game.player.PlayerManager;
import io.github.pulsebeat02.murderrun.locale.Message;
import java.awt.Color;
import org.bukkit.Material;
import org.bukkit.entity.Item;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;

public final class StarTrap extends SurvivorTrap {

  public StarTrap() {
    super(
      "star",
      Material.FIREWORK_STAR,
      Message.STAR_NAME.build(),
      Message.STAR_LORE.build(),
      Message.STAR_ACTIVATE.build(),
      GameProperties.STAR_COST,
      new Color(255, 215, 0)
    );
  }

  @Override
  public void onTrapActivate(final Game game, final GamePlayer murderer, final Item item) {
    final PlayerManager manager = game.getPlayerManager();
    manager.applyToLivingSurvivors(this::addPotionEffect);
    manager.playSoundForAllParticipants(GameProperties.STAR_SOUND);
  }

  private void addPotionEffect(final GamePlayer player) {
    final int duration = GameProperties.STAR_DURATION;
    player.addPotionEffects(
      new PotionEffect(PotionEffectType.SPEED, duration, 2),
      new PotionEffect(PotionEffectType.RESISTANCE, duration, 2),
      new PotionEffect(PotionEffectType.REGENERATION, duration, 2)
    );
  }
}
