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
import me.brandonli.murderrun.game.gadget.packet.GadgetDropPacket;
import me.brandonli.murderrun.game.gadget.packet.GadgetRightClickPacket;
import me.brandonli.murderrun.game.gadget.survivor.SurvivorGadget;
import me.brandonli.murderrun.game.player.GamePlayerManager;
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

public final class FlashBang extends SurvivorGadget implements Listener {

  private final Game game;

  public FlashBang(final Game game) {
    final GameProperties properties = game.getProperties();
    super(
      "flash_bang",
      properties.getFlashbangCost(),
      ItemFactory.createFlashBang(
        ItemFactory.createGadget(
          "flash_bang",
          properties.getFlashbangMaterial(),
          Message.FLASHBANG_NAME.build(),
          Message.FLASHBANG_LORE.build()
        )
      )
    );
    this.game = game;
  }

  @Override
  public boolean onGadgetDrop(final GadgetDropPacket packet) {
    return true;
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
    if (!PDCUtils.isFlashBang(stack)) {
      return;
    }

    final Location location = EventUtils.getProjectileLocation(event);
    if (location == null) {
      return;
    }

    final World world = requireNonNull(location.getWorld());
    world.spawnParticle(Particle.DUST, location, 25, 0.5, 0.5, 0.5, 0.5, new DustOptions(Color.YELLOW, 4));

    final GameProperties properties = this.game.getProperties();
    final GamePlayerManager manager = this.game.getPlayerManager();
    manager.applyToKillers(killer -> {
      final Location killerLocation = killer.getLocation();
      final double distance = killerLocation.distanceSquared(location);
      final double radius = properties.getFlashbangRadius();
      if (distance < radius * radius) {
        final int duration = properties.getFlashbangDuration();
        killer.addPotionEffects(
          new PotionEffect(PotionEffectType.BLINDNESS, duration, 0),
          new PotionEffect(PotionEffectType.SLOWNESS, duration, 4)
        );
      }
    });

    manager.playSoundForAllParticipantsAtLocation(location, Sounds.FLASHBANG);
  }
}
