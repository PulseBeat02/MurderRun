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
package me.brandonli.murderrun.game.gadget.survivor.tool;

import static java.util.Objects.requireNonNull;

import me.brandonli.murderrun.game.Game;
import me.brandonli.murderrun.game.GameProperties;
import me.brandonli.murderrun.game.gadget.packet.GadgetDropPacket;
import me.brandonli.murderrun.game.gadget.packet.GadgetRightClickPacket;
import me.brandonli.murderrun.game.gadget.survivor.SurvivorGadget;
import me.brandonli.murderrun.game.player.GamePlayer;
import me.brandonli.murderrun.game.player.GamePlayerManager;
import me.brandonli.murderrun.game.player.PlayerAudience;
import me.brandonli.murderrun.game.player.Survivor;
import me.brandonli.murderrun.game.scheduler.GameScheduler;
import me.brandonli.murderrun.game.scheduler.reference.LoosePlayerReference;
import me.brandonli.murderrun.locale.Message;
import me.brandonli.murderrun.resourcepack.sound.Sounds;
import me.brandonli.murderrun.utils.PDCUtils;
import me.brandonli.murderrun.utils.item.Item;
import me.brandonli.murderrun.utils.item.ItemFactory;
import org.bukkit.Color;
import org.bukkit.Location;
import org.bukkit.Particle;
import org.bukkit.Particle.DustOptions;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerFishEvent;
import org.bukkit.inventory.EquipmentSlot;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.PlayerInventory;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;
import org.bukkit.util.Vector;

public final class Flashlight extends SurvivorGadget implements Listener {

  private final Game game;

  public Flashlight(final Game game) {
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
    this.game = game;
  }

  @EventHandler
  public void onPlayerFish(final PlayerFishEvent event) {
    final PlayerFishEvent.State state = event.getState();
    if (state != PlayerFishEvent.State.FISHING) {
      return;
    }

    final Player player = event.getPlayer();
    final GamePlayerManager manager = this.game.getPlayerManager();
    if (!manager.checkPlayerExists(player)) {
      return;
    }

    final PlayerInventory inventory = player.getInventory();
    final EquipmentSlot hand = requireNonNull(event.getHand());
    final ItemStack rod = requireNonNull(inventory.getItem(hand));
    if (!PDCUtils.isFlashlight(rod)) {
      return;
    }

    event.setCancelled(true);
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

    final Game game = packet.getGame();
    final GamePlayer player = packet.getPlayer();
    final int cooldown = player.getCooldown(stack);
    if (cooldown > 0) {
      return true;
    }

    Item.builder(stack).useOneDurability();

    final int cooldownDuration = (int) (GameProperties.FLASHLIGHT_COOLDOWN * 20);
    player.setCooldown(stack, cooldownDuration);

    final GameScheduler scheduler = game.getScheduler();
    if (player instanceof final Survivor survivor) {
      survivor.setCanSee(true);
      final LoosePlayerReference reference = LoosePlayerReference.of(player);
      final int duration = GameProperties.FLASHLIGHT_DURATION;
      scheduler.scheduleTask(() -> survivor.setCanSee(false), duration, reference);
    }

    final PlayerAudience audience = player.getAudience();
    audience.playSound(Sounds.FLASHLIGHT);
    this.sprayParticlesInCone(game, player);

    return false;
  }

  private void sprayParticlesInCone(final Game game, final GamePlayer player) {
    final GamePlayerManager manager = game.getPlayerManager();
    final Location handLocation = player.getEyeLocation();
    final Vector direction = handLocation.getDirection();
    final double increment = Math.toRadians(5);
    final double maxAngle = Math.toRadians(GameProperties.FLASHLIGHT_CONE_ANGLE);
    for (double t = 0; t < GameProperties.FLASHLIGHT_CONE_LENGTH; t += 0.5) {
      for (double angle = -maxAngle; angle <= maxAngle; angle += increment) {
        final Location particleLocation = this.getParticleLocation(direction, handLocation, t, angle);
        manager.applyToAllParticipants(participant -> {
          if (participant == player) {
            return;
          }
          participant.spawnParticle(Particle.DUST, particleLocation, 1, 0, 0, 0, 0, new DustOptions(Color.YELLOW, 3));
        });
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
