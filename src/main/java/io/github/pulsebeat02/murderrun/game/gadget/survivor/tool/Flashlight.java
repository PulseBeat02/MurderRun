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
package io.github.pulsebeat02.murderrun.game.gadget.survivor.tool;

import static java.util.Objects.requireNonNull;

import io.github.pulsebeat02.murderrun.game.Game;
import io.github.pulsebeat02.murderrun.game.GameProperties;
import io.github.pulsebeat02.murderrun.game.gadget.packet.GadgetDropPacket;
import io.github.pulsebeat02.murderrun.game.gadget.packet.GadgetRightClickPacket;
import io.github.pulsebeat02.murderrun.game.gadget.survivor.SurvivorGadget;
import io.github.pulsebeat02.murderrun.game.player.GamePlayer;
import io.github.pulsebeat02.murderrun.game.player.GamePlayerManager;
import io.github.pulsebeat02.murderrun.game.player.PlayerAudience;
import io.github.pulsebeat02.murderrun.locale.Message;
import io.github.pulsebeat02.murderrun.resourcepack.sound.Sounds;
import io.github.pulsebeat02.murderrun.utils.item.Item;
import io.github.pulsebeat02.murderrun.utils.item.ItemFactory;
import org.bukkit.Color;
import org.bukkit.Location;
import org.bukkit.Particle;
import org.bukkit.Particle.DustOptions;
import org.bukkit.World;
import org.bukkit.inventory.ItemStack;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;
import org.bukkit.util.Vector;

public final class Flashlight extends SurvivorGadget {

  public Flashlight() {
    super(
      "flashlight",
      GameProperties.FLASHLIGHT_COST,
      ItemFactory.createFlashlight(
        ItemFactory.createGadget(
          "flashlight",
          GameProperties.FLASHLIGHT_MATERIAL,
          Message.FLASHLIGHT_NAME.build(),
          Message.FLASHLIGHT_LORE.build()
        )
      )
    );
  }

  @Override
  public boolean onGadgetDrop(final GadgetDropPacket packet) {
    return true;
  }

  @Override
  public boolean onGadgetRightClick(final GadgetRightClickPacket packet) {
    final ItemStack stack = packet.getItemStack();
    if (stack == null) {
      return true;
    }

    Item.builder(stack).useOneDurability();

    final Game game = packet.getGame();
    final GamePlayer player = packet.getPlayer();
    this.sprayParticlesInCone(game, player);

    final PlayerAudience audience = player.getAudience();
    audience.playSound(Sounds.FLASHLIGHT);

    return false;
  }

  private void sprayParticlesInCone(final Game game, final GamePlayer player) {
    final GamePlayerManager manager = game.getPlayerManager();
    final Location handLocation = player.getEyeLocation();
    final World world = requireNonNull(handLocation.getWorld());
    final Vector direction = handLocation.getDirection();
    final double increment = Math.toRadians(5);
    final double maxAngle = Math.toRadians(GameProperties.FLASHLIGHT_CONE_ANGLE);
    for (double t = 0; t < GameProperties.FLASHLIGHT_CONE_LENGTH; t += 0.5) {
      for (double angle = -maxAngle; angle <= maxAngle; angle += increment) {
        final Location particleLocation = this.getParticleLocation(direction, handLocation, t, angle);
        world.spawnParticle(Particle.DUST, particleLocation, 1, 0, 0, 0, 0, new DustOptions(Color.YELLOW, 3));
        manager.applyToKillers(killer -> this.applyPotionEffects(killer, particleLocation));
      }
    }
  }

  private Location getParticleLocation(final Vector direction, final Location handLocation, final double t, final double angle) {
    final Vector copy = direction.clone();
    final Vector offset = copy.multiply(t);
    offset.rotateAroundY(angle);

    final Location hand = handLocation.clone();
    return hand.add(offset);
  }

  private void applyPotionEffects(final GamePlayer killer, final Location particleLocation) {
    final Location killerLocation = killer.getLocation();
    final double distance = killerLocation.distanceSquared(particleLocation);
    final double radius = GameProperties.FLASHLIGHT_RADIUS;
    if (distance < radius * radius) {
      killer.addPotionEffects(new PotionEffect(PotionEffectType.BLINDNESS, 5 * 20, Integer.MAX_VALUE));
    }
  }
}
