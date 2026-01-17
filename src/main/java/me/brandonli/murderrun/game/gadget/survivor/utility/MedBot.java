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

import com.google.common.util.concurrent.AtomicDouble;
import java.util.concurrent.atomic.AtomicLong;
import java.util.function.Consumer;
import me.brandonli.murderrun.game.Game;
import me.brandonli.murderrun.game.GameProperties;
import me.brandonli.murderrun.game.gadget.packet.GadgetDropPacket;
import me.brandonli.murderrun.game.gadget.survivor.SurvivorGadget;
import me.brandonli.murderrun.game.player.GamePlayer;
import me.brandonli.murderrun.game.player.GamePlayerManager;
import me.brandonli.murderrun.game.player.PlayerAudience;
import me.brandonli.murderrun.game.scheduler.GameScheduler;
import me.brandonli.murderrun.game.scheduler.reference.EntityReference;
import me.brandonli.murderrun.locale.Message;
import me.brandonli.murderrun.utils.item.Item;
import me.brandonli.murderrun.utils.item.ItemFactory;
import net.kyori.adventure.text.Component;
import org.bukkit.Color;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.Particle;
import org.bukkit.Particle.DustOptions;
import org.bukkit.World;
import org.bukkit.entity.ArmorStand;
import org.bukkit.entity.EntityType;
import org.bukkit.inventory.EntityEquipment;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;
import org.bukkit.util.EulerAngle;

public final class MedBot extends SurvivorGadget {

  public MedBot(final Game game) {
    final GameProperties properties = game.getProperties();
    super(
        "med_bot",
        properties.getMedBotCost(),
        ItemFactory.createGadget(
            "med_bot",
            properties.getMedBotMaterial(),
            Message.MED_BOT_NAME.build(),
            Message.MED_BOT_LORE.build()));
  }

  @Override
  public boolean onGadgetDrop(final GadgetDropPacket packet) {
    final Game game = packet.getGame();
    final GamePlayer player = packet.getPlayer();
    final org.bukkit.entity.Item item = packet.getItem();
    item.remove();

    final GamePlayerManager manager = game.getPlayerManager();
    final Location location = player.getLocation();
    final World world = requireNonNull(location.getWorld());
    final ArmorStand armorStand = (ArmorStand) world.spawnEntity(location, EntityType.ARMOR_STAND);
    armorStand.setInvisible(true);
    armorStand.setGravity(false);
    armorStand.setMarker(true);

    final EntityEquipment equipment = requireNonNull(armorStand.getEquipment());
    equipment.setHelmet(Item.create(Material.CHORUS_FLOWER));

    final GameScheduler scheduler = game.getScheduler();
    this.handleRotation(scheduler, armorStand);
    this.handleVerticalMotion(scheduler, armorStand);
    this.handleParticles(scheduler, armorStand);
    this.handleMedBotUpdate(scheduler, manager, armorStand);

    final GameProperties properties = game.getProperties();
    final PlayerAudience audience = player.getAudience();
    audience.playSound(properties.getMedBotSound());

    return false;
  }

  private void handleMedBotUpdate(
      final GameScheduler scheduler, final GamePlayerManager manager, final ArmorStand stand) {
    final Consumer<GamePlayer> consumer = survivor -> this.handleInnocentEffects(survivor, stand);
    final Consumer<GamePlayer> killerConsumer =
        killer -> this.handleKillerDestroy(manager, killer, stand);
    final Runnable task = () -> {
      manager.applyToLivingSurvivors(consumer);
      manager.applyToKillers(killerConsumer);
    };
    final EntityReference reference = EntityReference.of(stand);
    scheduler.scheduleRepeatedTask(task, 0, 5L, reference);
  }

  private void handleKillerDestroy(
      final GamePlayerManager manager, final GamePlayer killer, final ArmorStand stand) {
    final Location origin = stand.getLocation();
    final Location location = killer.getLocation();
    final World first = stand.getWorld();
    final World second = location.getWorld();
    if (first != second) {
      return;
    }

    final Game game = manager.getGame();
    final GameProperties properties = game.getProperties();
    final double distance = origin.distanceSquared(location);
    final double radius = properties.getMedBotDestroyRadius();
    if (distance < radius * radius) {
      final Component message = Message.MED_BOT_DEACTIVATE.build();
      manager.sendMessageToAllLivingSurvivors(message);
      stand.remove();
    }
  }

  private void handleInnocentEffects(final GamePlayer innocent, final ArmorStand stand) {
    final Location origin = stand.getLocation();
    final Location location = innocent.getLocation();
    final double distance = origin.distanceSquared(location);
    final Game game = innocent.getGame();
    final GameProperties properties = game.getProperties();
    final double radius = properties.getMedBotRadius();
    if (distance < radius * radius) {
      innocent.addPotionEffects(new PotionEffect(PotionEffectType.REGENERATION, 2 * 20, 3));
    }
  }

  private void handleRotation(final GameScheduler scheduler, final ArmorStand stand) {
    final EntityReference reference = EntityReference.of(stand);
    scheduler.scheduleRepeatedTask(() -> this.rotateOneIteration(stand), 0, 1, reference);
  }

  private void handleVerticalMotion(final GameScheduler scheduler, final ArmorStand stand) {
    final AtomicDouble lastYOffset = new AtomicDouble();
    final AtomicLong currentTick = new AtomicLong();
    final Runnable task = () -> lastYOffset.set(
        this.moveVerticallyOneIteration(stand, currentTick.getAndIncrement(), lastYOffset.get()));
    final EntityReference reference = EntityReference.of(stand);
    scheduler.scheduleRepeatedTask(task, 0, 1, reference);
  }

  private void handleParticles(final GameScheduler scheduler, final ArmorStand stand) {
    final EntityReference reference = EntityReference.of(stand);
    scheduler.scheduleRepeatedTask(() -> this.spawnParticle(stand), 0, 2, reference);
  }

  private void spawnParticle(final ArmorStand stand) {
    final Location location = stand.getLocation();
    final World world = requireNonNull(location.getWorld());
    world.spawnParticle(Particle.DUST, location, 5, 8, 8, 8, new DustOptions(Color.PURPLE, 4));
  }

  private double moveVerticallyOneIteration(
      final ArmorStand stand, final long current, final double lastYOffset) {
    final double yOffset = Math.sin(Math.toRadians(current * 5.0d));
    stand.teleport(stand.getLocation().add(0, (yOffset - lastYOffset), 0));
    return yOffset;
  }

  private void rotateOneIteration(final ArmorStand stand) {
    final EulerAngle angle = stand.getHeadPose();
    final EulerAngle result = angle.add(0, 0.1, 0);
    stand.setHeadPose(result);
  }
}
