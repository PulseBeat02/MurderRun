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
package me.brandonli.murderrun.game.gadget.killer.utility.tool;

import static java.util.Objects.requireNonNull;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;
import me.brandonli.murderrun.game.Game;
import me.brandonli.murderrun.game.GameProperties;
import me.brandonli.murderrun.game.gadget.killer.KillerGadget;
import me.brandonli.murderrun.game.gadget.packet.GadgetDropPacket;
import me.brandonli.murderrun.game.player.GamePlayer;
import me.brandonli.murderrun.game.player.GamePlayerManager;
import me.brandonli.murderrun.game.player.PlayerAudience;
import me.brandonli.murderrun.game.scheduler.GameScheduler;
import me.brandonli.murderrun.game.scheduler.reference.NullReference;
import me.brandonli.murderrun.locale.Message;
import me.brandonli.murderrun.resourcepack.sound.Sounds;
import me.brandonli.murderrun.utils.PDCUtils;
import me.brandonli.murderrun.utils.immutable.Keys;
import me.brandonli.murderrun.utils.item.ItemFactory;
import org.bukkit.Color;
import org.bukkit.Location;
import org.bukkit.Particle;
import org.bukkit.Particle.DustOptions;
import org.bukkit.World;
import org.bukkit.entity.Arrow;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;
import org.bukkit.entity.Projectile;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.ProjectileHitEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.PlayerInventory;
import org.bukkit.persistence.PersistentDataType;
import org.bukkit.projectiles.ProjectileSource;
import org.bukkit.scheduler.BukkitTask;
import org.bukkit.util.Vector;
import org.incendo.cloud.type.tuple.Pair;

public final class PortalGun extends KillerGadget implements Listener {

  private final Map<String, Pair<Portal, Portal>> portals;
  private final Game game;

  public PortalGun(final Game game) {
    final GameProperties properties = game.getProperties();
    super(
      "portal_gun",
      properties.getPortalGunCost(),
      ItemFactory.createPortalGun(
        ItemFactory.createGadget(
          "portal_gun",
          properties.getPortalGunMaterial(),
          Message.PORTAL_GUN_NAME.build(),
          Message.PORTAL_GUN_LORE.build()
        )
      )
    );
    this.portals = new HashMap<>();
    this.game = game;
  }

  @Override
  public boolean onGadgetDrop(final GadgetDropPacket packet) {
    return true;
  }

  @EventHandler(priority = EventPriority.LOWEST)
  public void onProjectileHit(final ProjectileHitEvent event) {
    final Entity entity = event.getEntity();
    if (!(entity instanceof final Arrow arrow)) {
      return;
    }

    final ProjectileSource shooter = arrow.getShooter();
    if (!(shooter instanceof final Player player)) {
      return;
    }

    final PlayerInventory inventory = player.getInventory();
    final ItemStack stack = inventory.getItemInMainHand();
    if (!PDCUtils.isPortalGun(stack)) {
      return;
    }

    final GamePlayerManager manager = this.game.getPlayerManager();
    if (!manager.checkPlayerExists(player)) {
      return;
    }

    final GamePlayer gamePlayer = manager.getGamePlayer(player);
    final PlayerAudience audience = gamePlayer.getAudience();
    audience.playSound(Sounds.PORTAL);

    final Boolean status = PDCUtils.getPersistentDataAttribute(stack, Keys.PORTAL_GUN, PersistentDataType.BOOLEAN);
    final String uuid = PDCUtils.getPersistentDataAttribute(stack, Keys.UUID, PersistentDataType.STRING);
    if (status == null || uuid == null) {
      return;
    }

    Pair<Portal, Portal> pair = this.portals.get(uuid);
    if (pair == null) {
      final Portal emptyPortal = new Portal(null);
      final Portal emptyPortal2 = new Portal(null);
      final Pair<Portal, Portal> empty = Pair.of(emptyPortal, emptyPortal2);
      this.portals.put(uuid, empty);
    }

    // true -> spawn sending portal
    // false -> spawn receiving portal
    pair = requireNonNull(this.portals.get(uuid));
    final GameScheduler scheduler = this.game.getScheduler();
    final Location raw = arrow.getLocation();
    final Location teleportLocation = raw.add(0, -1, 0);
    final Portal holder = new Portal(teleportLocation);
    final Portal check = pair.first();
    if (status && check.isValidPortal()) {
      final UUID random = UUID.randomUUID();
      final String data = random.toString();
      PDCUtils.setPersistentDataAttribute(stack, Keys.PORTAL_GUN, PersistentDataType.BOOLEAN, false);
      PDCUtils.setPersistentDataAttribute(stack, Keys.UUID, PersistentDataType.STRING, data);
      final Projectile projectile = event.getEntity();
      projectile.remove();
      event.setCancelled(true);
      this.onProjectileHit(event);
      return;
    }

    final boolean first;
    if (status) {
      final Portal receiving = pair.second();
      final Pair<Portal, Portal> value = Pair.of(holder, receiving);
      this.portals.put(uuid, value);
      first = true;
    } else {
      final Portal sending = pair.first();
      final Pair<Portal, Portal> value = Pair.of(sending, holder);
      this.portals.put(uuid, value);
      first = false;
    }
    PDCUtils.setPersistentDataAttribute(stack, Keys.PORTAL_GUN, PersistentDataType.BOOLEAN, !status);

    final Pair<Portal, Portal> newPair = this.portals.get(uuid);
    final Location location = raw.add(0, 2, 0);
    final int[] ids = this.spawnPortal(manager, scheduler, newPair, location);
    final Portal portal = first ? pair.first() : pair.second();
    for (final Integer id : ids) {
      portal.addTask(id);
    }
  }

  private int[] spawnPortal(
    final GamePlayerManager manager,
    final GameScheduler scheduler,
    final Pair<Portal, Portal> parent,
    final Location center
  ) {
    final int first = this.spawnPortalEffects(scheduler, center);
    final int second = this.handlePortalTeleportationLogic(manager, scheduler, parent);
    return new int[] { first, second };
  }

  private int handlePortalTeleportationLogic(
    final GamePlayerManager manager,
    final GameScheduler scheduler,
    final Pair<Portal, Portal> parent
  ) {
    final Portal sending = parent.first();
    final Portal receiving = parent.second();
    if (sending.isInvalidPortal() || receiving.isInvalidPortal()) {
      return -1;
    }

    final Location sendingLocation = requireNonNull(sending.getLocation());
    final Location receivingLocation = requireNonNull(receiving.getLocation());
    final NullReference reference = NullReference.of();
    final BukkitTask bukkitTask = scheduler.scheduleRepeatedTask(
      () -> this.handleParticipants(manager, sendingLocation, receivingLocation),
      0L,
      20L,
      reference
    );
    return bukkitTask.getTaskId();
  }

  private void handleParticipants(final GamePlayerManager manager, final Location sendingLocation, final Location receivingLocation) {
    manager.applyToAllParticipants(player -> this.handleTeleports(player, sendingLocation, receivingLocation));
  }

  private void handleTeleports(final GamePlayer player, final Location sendingLocation, final Location receivingLocation) {
    final long last = player.getLastPortalUse();
    final long current = System.currentTimeMillis();
    if (current - last < 2000L) {
      return;
    }

    final Location playerLocation = player.getLocation();
    final double distance1 = playerLocation.distanceSquared(sendingLocation);
    if (distance1 < 9) {
      player.setLastPortalUse(current);
      final Location receiving = receivingLocation.clone();
      final Vector direction = playerLocation.getDirection();
      receiving.setDirection(direction);
      player.teleport(receiving);
    }

    final double distance2 = playerLocation.distanceSquared(receivingLocation);
    if (distance2 < 9) {
      player.setLastPortalUse(current);
      final Location sending = sendingLocation.clone();
      final Vector direction = playerLocation.getDirection();
      sending.setDirection(direction);
      player.teleport(sending);
    }
  }

  private int spawnPortalEffects(final GameScheduler scheduler, final Location center) {
    final World world = requireNonNull(center.getWorld());
    final double radiusX = 0.75d;
    final double radiusY = 1.5d;
    final int particleCount = 40;
    final int insideParticleCount = 20;
    final NullReference reference = NullReference.of();
    final Runnable task = () -> this.handlePortalEffects(center, particleCount, radiusX, radiusY, world, insideParticleCount);
    final BukkitTask bukkitTask = scheduler.scheduleRepeatedTask(task, 0L, 2L, reference);
    return bukkitTask.getTaskId();
  }

  private void handlePortalEffects(
    final Location center,
    final int particleCount,
    final double radiusX,
    final double radiusY,
    final World world,
    final int insideParticleCount
  ) {
    this.spawnPortalFrame(center, particleCount, radiusX, radiusY, world);
    this.fillPortal(center, insideParticleCount, radiusX, radiusY, world);
  }

  private void fillPortal(
    final Location center,
    final int insideParticleCount,
    final double radiusX,
    final double radiusY,
    final World world
  ) {
    for (int i = 0; i < insideParticleCount; i++) {
      for (int j = 0; j < insideParticleCount; j++) {
        final double angle = (2 * Math.PI * i) / insideParticleCount;
        final double radius = (double) j / insideParticleCount;
        final double x = center.getX() + radiusX * radius * Math.cos(angle);
        final double y = center.getY() + radiusY * radius * Math.sin(angle);
        final Location particleLocation = new Location(world, x, y, center.getZ());
        world.spawnParticle(Particle.DUST, particleLocation, 1, new DustOptions(Color.BLUE, 1));
      }
    }
  }

  private void spawnPortalFrame(
    final Location center,
    final int particleCount,
    final double radiusX,
    final double radiusY,
    final World world
  ) {
    for (int i = 0; i < particleCount; i++) {
      final double angle = (2 * Math.PI * i) / particleCount;
      final double x = center.getX() + radiusX * Math.cos(angle);
      final double y = center.getY() + radiusY * Math.sin(angle);
      final Location particleLocation = new Location(world, x, y, center.getZ());
      world.spawnParticle(Particle.DUST, particleLocation, 1, new DustOptions(Color.YELLOW, 1));
    }
  }
}
