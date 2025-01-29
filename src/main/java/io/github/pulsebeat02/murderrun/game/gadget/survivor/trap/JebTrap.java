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
import io.github.pulsebeat02.murderrun.utils.RandomUtils;
import java.awt.Color;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.Particle;
import org.bukkit.Particle.DustOptions;
import org.bukkit.World;
import org.bukkit.entity.Item;
import org.bukkit.entity.Sheep;

public final class JebTrap extends SurvivorTrap {

  public JebTrap() {
    super(
      "jeb",
      Material.CYAN_WOOL,
      Message.JEB_NAME.build(),
      Message.JEB_LORE.build(),
      Message.JEB_ACTIVATE.build(),
      GameProperties.JEB_COST,
      Color.WHITE
    );
  }

  @Override
  public void onTrapActivate(final Game game, final GamePlayer murderer, final Item item) {
    final Location location = murderer.getLocation();
    final World world = requireNonNull(location.getWorld());
    for (int i = 0; i < GameProperties.JEB_SHEEP_COUNT; i++) {
      world.spawn(location, Sheep.class, sheep -> sheep.setCustomName("jeb_"));
    }

    final NullReference reference = NullReference.of();
    final GameScheduler scheduler = game.getScheduler();
    scheduler.scheduleRepeatedTask(() -> this.spawnRainbowParticles(location), 0, 5, GameProperties.JEB_DURATION, reference);

    final GamePlayerManager manager = game.getPlayerManager();
    manager.playSoundForAllParticipants(GameProperties.JEB_SOUND);
  }

  private void spawnRainbowParticles(final Location location) {
    final World world = requireNonNull(location.getWorld());
    final int r = RandomUtils.generateInt(255);
    final int g = RandomUtils.generateInt(255);
    final int b = RandomUtils.generateInt(255);
    final org.bukkit.Color color = org.bukkit.Color.fromRGB(r, g, b);
    world.spawnParticle(Particle.DUST, location, 15, 3, 3, 3, new DustOptions(color, 4));
  }
}
