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
package io.github.pulsebeat02.murderrun.game.gadget.survivor.utility;

import static java.util.Objects.requireNonNull;

import io.github.pulsebeat02.murderrun.game.Game;
import io.github.pulsebeat02.murderrun.game.GameProperties;
import io.github.pulsebeat02.murderrun.game.GameStatus;
import io.github.pulsebeat02.murderrun.game.gadget.packet.GadgetRightClickPacket;
import io.github.pulsebeat02.murderrun.game.gadget.survivor.SurvivorGadget;
import io.github.pulsebeat02.murderrun.game.player.GamePlayerManager;
import io.github.pulsebeat02.murderrun.game.scheduler.GameScheduler;
import io.github.pulsebeat02.murderrun.game.scheduler.reference.NullReference;
import io.github.pulsebeat02.murderrun.locale.Message;
import io.github.pulsebeat02.murderrun.resourcepack.sound.Sounds;
import io.github.pulsebeat02.murderrun.utils.EventUtils;
import io.github.pulsebeat02.murderrun.utils.PDCUtils;
import io.github.pulsebeat02.murderrun.utils.item.ItemFactory;
import org.bukkit.Color;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.Particle;
import org.bukkit.Particle.DustOptions;
import org.bukkit.World;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Snowball;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.ProjectileHitEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;

public final class SmokeGrenade extends SurvivorGadget implements Listener {

  private final Game game;

  public SmokeGrenade(final Game game) {
    super(
      "smoke_grenade",
      Material.SNOWBALL,
      Message.SMOKE_BOMB_NAME.build(),
      Message.SMOKE_BOMB_LORE.build(),
      GameProperties.SMOKE_GRENADE_COST,
      ItemFactory::createSmokeGrenade
    );
    this.game = game;
  }

  @Override
  public boolean onGadgetRightClick(final GadgetRightClickPacket packet) {
    final GameStatus status = this.game.getStatus();
    final GameStatus.Status gameStatus = status.getStatus();
    return gameStatus != GameStatus.Status.KILLERS_RELEASED;
  }

  @EventHandler(priority = EventPriority.LOWEST)
  public void onProjectileHitEvent(final ProjectileHitEvent event) {
    final Entity entity = event.getEntity();
    if (!(entity instanceof final Snowball snowball)) {
      return;
    }

    final ItemStack stack = snowball.getItem();
    if (!PDCUtils.isSmokeGrenade(stack)) {
      return;
    }

    final Location location = EventUtils.getProjectileLocation(event);
    if (location == null) {
      return;
    }

    final World world = requireNonNull(location.getWorld());
    final GameScheduler scheduler = this.game.getScheduler();
    final int duration = GameProperties.SMOKE_GRENADE_DURATION;
    final NullReference reference = NullReference.of();
    final Runnable task = () -> world.spawnParticle(Particle.DUST, location, 10, 1, 1, 1, new DustOptions(Color.GRAY, 4));
    scheduler.scheduleRepeatedTask(task, 0, 1, duration, reference);

    final GamePlayerManager manager = this.game.getPlayerManager();
    manager.applyToKillers(player -> {
      final Location playerLocation = player.getLocation();
      final double distance = playerLocation.distanceSquared(location);
      final double radius = GameProperties.SMOKE_GRENADE_RADIUS;
      if (distance < radius * radius) {
        player.addPotionEffects(new PotionEffect(PotionEffectType.BLINDNESS, duration, Integer.MAX_VALUE));
      }
    });

    manager.playSoundForAllParticipantsAtLocation(location, Sounds.FLASHBANG);
  }
}
