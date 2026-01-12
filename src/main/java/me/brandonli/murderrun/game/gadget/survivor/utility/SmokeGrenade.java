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
package me.brandonli.murderrun.game.gadget.survivor.utility;

import static java.util.Objects.requireNonNull;

import me.brandonli.murderrun.game.Game;
import me.brandonli.murderrun.game.GameProperties;
import me.brandonli.murderrun.game.GameStatus;
import me.brandonli.murderrun.game.gadget.packet.GadgetRightClickPacket;
import me.brandonli.murderrun.game.gadget.survivor.SurvivorGadget;
import me.brandonli.murderrun.game.player.GamePlayerManager;
import me.brandonli.murderrun.game.scheduler.GameScheduler;
import me.brandonli.murderrun.game.scheduler.reference.NullReference;
import me.brandonli.murderrun.locale.Message;
import me.brandonli.murderrun.resourcepack.sound.Sounds;
import me.brandonli.murderrun.utils.EventUtils;
import me.brandonli.murderrun.utils.PDCUtils;
import me.brandonli.murderrun.utils.item.ItemFactory;
import org.bukkit.Color;
import org.bukkit.Location;
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
    final GameProperties properties = game.getProperties();
    super(
      "smoke_bomb",
      properties.getSmokeGrenadeCost(),
      ItemFactory.createSmokeGrenade(
        ItemFactory.createGadget(
          "smoke_bomb",
          properties.getSmokeGrenadeMaterial(),
          Message.SMOKE_BOMB_NAME.build(),
          Message.SMOKE_BOMB_LORE.build()
        )
      )
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
    final GameProperties properties = this.game.getProperties();
    final int duration = properties.getSmokeGrenadeDuration();
    final NullReference reference = NullReference.of();
    final Runnable task = () -> world.spawnParticle(Particle.DUST, location, 10, 1, 1, 1, new DustOptions(Color.GRAY, 4));
    scheduler.scheduleRepeatedTask(task, 0, 1, duration, reference);

    final GamePlayerManager manager = this.game.getPlayerManager();
    manager.applyToKillers(player -> {
      final Location playerLocation = player.getLocation();
      final double distance = playerLocation.distanceSquared(location);
      final double radius = properties.getSmokeGrenadeRadius();
      if (distance < radius * radius) {
        player.addPotionEffects(new PotionEffect(PotionEffectType.BLINDNESS, duration, Integer.MAX_VALUE));
      }
    });

    manager.playSoundForAllParticipantsAtLocation(location, Sounds.FLASHBANG);
  }
}
