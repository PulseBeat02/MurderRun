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

import static java.util.Objects.requireNonNull;

import io.github.pulsebeat02.murderrun.game.Game;
import io.github.pulsebeat02.murderrun.game.GameProperties;
import io.github.pulsebeat02.murderrun.game.player.GamePlayer;
import io.github.pulsebeat02.murderrun.game.player.GamePlayerManager;
import io.github.pulsebeat02.murderrun.game.scheduler.GameScheduler;
import io.github.pulsebeat02.murderrun.game.scheduler.reference.NullReference;
import io.github.pulsebeat02.murderrun.locale.Message;
import io.github.pulsebeat02.murderrun.resourcepack.sound.Sounds;
import io.github.pulsebeat02.murderrun.utils.item.ItemFactory;
import org.bukkit.Location;
import org.bukkit.Particle;
import org.bukkit.Particle.DustOptions;
import org.bukkit.World;
import org.bukkit.entity.Item;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;

public final class FartTrap extends SurvivorTrap {

  public FartTrap() {
    super(
      "fart",
      GameProperties.FART_COST,
      ItemFactory.createGadget("fart", GameProperties.FART_MATERIAL, Message.FART_NAME.build(), Message.FART_LORE.build()),
      Message.FART_ACTIVATE.build(),
      GameProperties.FART_COLOR
    );
  }

  @Override
  public void onTrapActivate(final Game game, final GamePlayer murderer, final Item item) {
    final int duration = GameProperties.FART_EFFECT_DURATION;
    murderer.addPotionEffects(
      new PotionEffect(PotionEffectType.SLOWNESS, duration, 4),
      new PotionEffect(PotionEffectType.NAUSEA, duration, 1)
    );

    final GameScheduler scheduler = game.getScheduler();
    final Location location = murderer.getLocation();
    final NullReference reference = NullReference.of();
    scheduler.scheduleRepeatedTask(() -> this.spawnParticles(location), 0, 5, GameProperties.FART_DURATION, reference);

    final GamePlayerManager manager = game.getPlayerManager();
    manager.playSoundForAllParticipants(Sounds.FART);
  }

  private void spawnParticles(final Location location) {
    final World world = requireNonNull(location.getWorld());
    world.spawnParticle(Particle.DUST, location, 10, 2, 2, 2, new DustOptions(org.bukkit.Color.GREEN, 4));
  }
}
