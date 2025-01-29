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
package io.github.pulsebeat02.murderrun.game.gadget.killer.utility;

import io.github.pulsebeat02.murderrun.game.Game;
import io.github.pulsebeat02.murderrun.game.GameProperties;
import io.github.pulsebeat02.murderrun.game.gadget.killer.KillerGadget;
import io.github.pulsebeat02.murderrun.game.gadget.packet.GadgetDropPacket;
import io.github.pulsebeat02.murderrun.game.player.GamePlayer;
import io.github.pulsebeat02.murderrun.game.player.GamePlayerManager;
import io.github.pulsebeat02.murderrun.game.player.PlayerAudience;
import io.github.pulsebeat02.murderrun.game.scheduler.GameScheduler;
import io.github.pulsebeat02.murderrun.game.scheduler.reference.NullReference;
import io.github.pulsebeat02.murderrun.locale.Message;
import io.github.pulsebeat02.murderrun.utils.item.ItemFactory;
import net.kyori.adventure.text.Component;
import org.bukkit.Color;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.Particle;
import org.bukkit.Particle.DustOptions;
import org.bukkit.World;
import org.bukkit.entity.Item;

public final class RedArrow extends KillerGadget {

  public RedArrow() {
    super(
      "red_arrow",
      Material.TIPPED_ARROW,
      Message.RED_ARROW_NAME.build(),
      Message.RED_ARROW_LORE.build(),
      GameProperties.RED_ARROW_COST,
      ItemFactory::createRedArrow
    );
  }

  @Override
  public boolean onGadgetDrop(final GadgetDropPacket packet) {
    final Game game = packet.getGame();
    final GamePlayer player = packet.getPlayer();
    final Item item = packet.getItem();
    item.remove();

    final GamePlayerManager manager = game.getPlayerManager();
    final GameScheduler scheduler = game.getScheduler();
    final NullReference reference = NullReference.of();
    scheduler.scheduleRepeatedTask(() -> this.handleSurvivors(manager), 0, GameProperties.RED_ARROW_DURATION, reference);

    final PlayerAudience audience = player.getAudience();
    final Component message = Message.RED_ARROW_ACTIVATE.build();
    audience.sendMessage(message);
    audience.playSound(GameProperties.RED_ARROW_SOUND);

    return false;
  }

  private void handleSurvivors(final GamePlayerManager manager) {
    manager.applyToLivingSurvivors(this::spawnParticleBeam);
  }

  private void spawnParticleBeam(final GamePlayer player) {
    final Location location = player.getLocation();
    final World world = location.getWorld();
    if (world == null) {
      return;
    }

    final double startY = location.getY();
    final double skyLimit = world.getMaxHeight();
    final double x = location.getX();
    final double z = location.getZ();

    for (double y = startY; y <= skyLimit; y += 1.0) {
      final Location particleLocation = new Location(world, x, y, z);
      world.spawnParticle(Particle.DUST, particleLocation, 1, new DustOptions(Color.RED, 4));
    }
  }
}
