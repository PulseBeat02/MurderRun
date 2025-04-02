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
import io.github.pulsebeat02.murderrun.game.player.Participant;
import io.github.pulsebeat02.murderrun.locale.Message;
import io.github.pulsebeat02.murderrun.utils.item.ItemFactory;
import java.awt.Color;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.World;
import org.bukkit.entity.Item;
import org.bukkit.util.Vector;

public final class ShockwaveTrap extends SurvivorTrap {

  public ShockwaveTrap() {
    super(
      "shockwave",
      GameProperties.SHOCKWAVE_COST,
      ItemFactory.createGadget("shockwave", Material.TNT, Message.SHOCKWAVE_NAME.build(), Message.SHOCKWAVE_LORE.build()),
      Message.SHOCKWAVE_ACTIVATE.build(),
      new Color(255, 215, 0)
    );
  }

  @Override
  public void onTrapActivate(final Game game, final GamePlayer survivor, final Item item) {
    final Location origin = item.getLocation();
    final World world = requireNonNull(origin.getWorld());
    world.createExplosion(origin, 0, false, false);

    final GamePlayerManager manager = game.getPlayerManager();
    manager.applyToAllParticipants(participant -> this.applyShockwave(participant, origin));
    manager.playSoundForAllParticipants(GameProperties.SHOCKWAVE_SOUND);
  }

  private void applyShockwave(final Participant participant, final Location origin) {
    final Location location = participant.getLocation();
    final double distance = location.distanceSquared(origin);
    final double radius = GameProperties.SHOCKWAVE_EXPLOSION_RADIUS;
    if (distance < radius * radius) {
      final Vector playerVector = location.toVector();
      final Vector blockVector = origin.toVector();
      final Vector vector = playerVector.subtract(blockVector);
      vector.normalize();
      vector.multiply(GameProperties.SHOCKWAVE_EXPLOSION_POWER);
      participant.setVelocity(vector);
    }
  }
}
