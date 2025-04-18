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
import me.brandonli.murderrun.game.player.metadata.MetadataManager;
import me.brandonli.murderrun.game.scheduler.GameScheduler;
import me.brandonli.murderrun.game.scheduler.reference.StrictPlayerReference;
import me.brandonli.murderrun.locale.Message;
import me.brandonli.murderrun.utils.item.ItemFactory;
import org.bukkit.Particle;
import org.bukkit.entity.Item;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;

public final class HauntTrap extends SurvivorTrap {

  public HauntTrap() {
    super(
      "haunt_trap",
      GameProperties.HAUNT_COST,
      ItemFactory.createGadget("haunt_trap", GameProperties.HAUNT_MATERIAL, Message.HAUNT_NAME.build(), Message.HAUNT_LORE.build()),
      Message.HAUNT_ACTIVATE.build(),
      GameProperties.HAUNT_COLOR
    );
  }

  @Override
  public void onTrapActivate(final Game game, final GamePlayer murderer, final Item item) {
    final int duration = GameProperties.HAUNT_DURATION;
    murderer.addPotionEffects(
      new PotionEffect(PotionEffectType.NAUSEA, duration, 10),
      new PotionEffect(PotionEffectType.BLINDNESS, duration, 1),
      new PotionEffect(PotionEffectType.SLOWNESS, duration, 4)
    );

    final StrictPlayerReference reference = StrictPlayerReference.of(murderer);
    final GameScheduler scheduler = game.getScheduler();
    scheduler.scheduleRepeatedTask(() -> this.spook(game, murderer), 0, 20L, duration, reference);

    final GamePlayerManager manager = game.getPlayerManager();
    manager.playSoundForAllParticipants(GameProperties.HAUNT_SOUND);
  }

  private void spook(final Game game, final GamePlayer gamePlayer) {
    gamePlayer.addPotionEffects(new PotionEffect(PotionEffectType.DARKNESS, 20, 0));
    gamePlayer.spawnPlayerSpecificParticle(Particle.ELDER_GUARDIAN);

    final MetadataManager metadata = gamePlayer.getMetadataManager();
    metadata.setWorldBorderEffect(true);

    final StrictPlayerReference reference = StrictPlayerReference.of(gamePlayer);
    final GameScheduler scheduler = game.getScheduler();
    scheduler.scheduleTask(() -> this.unspook(gamePlayer), 19, reference);
  }

  private void unspook(final GamePlayer gamePlayer) {
    gamePlayer.removePotionEffect(PotionEffectType.DARKNESS);

    final MetadataManager metadata = gamePlayer.getMetadataManager();
    metadata.setWorldBorderEffect(false);
  }
}
