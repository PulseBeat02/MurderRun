package io.github.pulsebeat02.murderrun.game.gadget.killer.utility;

import static io.github.pulsebeat02.murderrun.immutable.Holder.empty;
import static java.util.Objects.requireNonNull;

import io.github.pulsebeat02.murderrun.game.Game;
import io.github.pulsebeat02.murderrun.game.gadget.killer.KillerGadget;
import io.github.pulsebeat02.murderrun.game.player.GamePlayer;
import io.github.pulsebeat02.murderrun.game.player.PlayerManager;
import io.github.pulsebeat02.murderrun.game.scheduler.GameScheduler;
import io.github.pulsebeat02.murderrun.immutable.Holder;
import io.github.pulsebeat02.murderrun.immutable.Keys;
import io.github.pulsebeat02.murderrun.locale.Message;
import io.github.pulsebeat02.murderrun.utils.ItemUtils;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;
import org.bukkit.Color;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.Particle;
import org.bukkit.Particle.DustOptions;
import org.bukkit.World;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.entity.Arrow;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.ProjectileHitEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.persistence.PersistentDataType;
import org.bukkit.projectiles.ProjectileSource;
import org.incendo.cloud.type.tuple.Pair;

public final class PortalGun extends KillerGadget implements Listener {

  private final Map<String, Pair<Holder<Location>, Holder<Location>>> portals;
  private final Game game;

  public PortalGun(final Game game) {
    super(
        "portal_gun",
        Material.BOW,
        Message.PORTAL_GUN_NAME.build(),
        Message.PORTAL_GUN_LORE.build(),
        64,
        stack -> {
          final UUID uuid = UUID.randomUUID();
          final String data = uuid.toString();
          ItemUtils.setPersistentDataAttribute(
              stack, Keys.PORTAL_GUN, PersistentDataType.BOOLEAN, true);
          ItemUtils.setPersistentDataAttribute(stack, Keys.UUID, PersistentDataType.STRING, data);
          stack.addEnchantment(Enchantment.INFINITY, 1);
        });
    this.portals = new HashMap<>();
    this.game = game;
  }

  @EventHandler
  public void onProjectileHit(final ProjectileHitEvent event) {

    final Entity entity = event.getEntity();
    if (!(entity instanceof final Arrow arrow)) {
      return;
    }

    final ProjectileSource shooter = arrow.getShooter();
    if (!(shooter instanceof final Player player)) {
      return;
    }

    final ItemStack stack = player.getItemInUse();
    if (stack == null) {
      return;
    }

    if (!ItemUtils.isPortalGun(stack)) {
      return;
    }

    final Boolean status =
        ItemUtils.getPersistentDataAttribute(stack, Keys.PORTAL_GUN, PersistentDataType.BOOLEAN);
    final String uuid =
        ItemUtils.getPersistentDataAttribute(stack, Keys.UUID, PersistentDataType.STRING);
    if (status == null || uuid == null) {
      return;
    }

    if (!this.portals.containsKey(uuid)) {
      final Pair<Holder<Location>, Holder<Location>> empty = Pair.of(empty(), empty());
      this.portals.put(uuid, empty);
    }

    // true -> spawn sending portal
    // false -> spawn receiving portal
    final Pair<Holder<Location>, Holder<Location>> pair = this.portals.get(uuid);
    final Location location = arrow.getLocation();
    final Holder<Location> holder = Holder.of(location);
    if (status) {
      final Holder<Location> receiving = pair.second();
      final Pair<Holder<Location>, Holder<Location>> value = Pair.of(holder, receiving);
      this.portals.put(uuid, value);
    } else {
      final Holder<Location> sending = pair.first();
      final Pair<Holder<Location>, Holder<Location>> value = Pair.of(sending, holder);
      this.portals.put(uuid, value);
    }
    ItemUtils.setPersistentDataAttribute(
        stack, Keys.PORTAL_GUN, PersistentDataType.BOOLEAN, !status);

    final PlayerManager manager = this.game.getPlayerManager();
    final GameScheduler scheduler = this.game.getScheduler();
    final Pair<Holder<Location>, Holder<Location>> newPair = this.portals.get(uuid);
    this.spawnPortal(manager, scheduler, newPair, location);
  }

  private void spawnPortal(
      final PlayerManager manager,
      final GameScheduler scheduler,
      final Pair<Holder<Location>, Holder<Location>> parent,
      final Location center) {
    this.spawnPortalParticles(scheduler, center);
    this.handlePortalTeleportationLogic(manager, scheduler, parent);
  }

  private void handlePortalTeleportationLogic(
      final PlayerManager manager,
      final GameScheduler scheduler,
      final Pair<Holder<Location>, Holder<Location>> parent) {

    final Holder<Location> sending = parent.first();
    final Holder<Location> receiving = parent.second();
    if (sending.isEmpty() || receiving.isEmpty()) {
      return;
    }

    final Location sendingLocation = sending.get();
    final Location receivingLocation = receiving.get();
    scheduler.scheduleRepeatedTask(
        () -> this.handleParticipants(manager, sendingLocation, receivingLocation), 0L, 2 * 20L);
  }

  private void handleParticipants(
      final PlayerManager manager,
      final Location sendingLocation,
      final Location receivingLocation) {
    manager.applyToAllParticipants(
        player -> this.handleTeleports(player, sendingLocation, receivingLocation));
  }

  private void handleTeleports(
      final GamePlayer player, final Location sendingLocation, final Location receivingLocation) {

    final Location playerLocation = player.getLocation();
    final double distance1 = playerLocation.distanceSquared(sendingLocation);
    if (distance1 < 1) {
      player.teleport(receivingLocation);
    }

    final double distance2 = playerLocation.distanceSquared(receivingLocation);
    if (distance2 < 1) {
      player.teleport(sendingLocation);
    }
  }

  private void spawnPortalParticles(final GameScheduler scheduler, final Location center) {
    final World world = requireNonNull(center.getWorld());
    final double radiusX = 0.5d;
    final double radiusY = 2d;
    final int particleCount = 20;
    final int insideParticleCount = 30;
    final Runnable task = () -> this.handlePortalEffects(
        center, particleCount, radiusX, radiusY, world, insideParticleCount);
    scheduler.scheduleRepeatedTask(task, 0L, 4L);
  }

  private void handlePortalEffects(
      final Location center,
      final int particleCount,
      final double radiusX,
      final double radiusY,
      final World world,
      final int insideParticleCount) {
    this.spawnPortalFrame(center, particleCount, radiusX, radiusY, world);
    this.fillPortal(center, insideParticleCount, radiusX, radiusY, world);
  }

  private void fillPortal(
      final Location center,
      final int insideParticleCount,
      final double radiusX,
      final double radiusY,
      final World world) {
    for (int i = 0; i < insideParticleCount; i++) {
      for (int j = 0; j < insideParticleCount; j++) {
        final double angle = 2 * Math.PI * i / insideParticleCount;
        final double radius = (double) j / insideParticleCount;
        final double x = center.getX() + radiusX * radius * Math.cos(angle);
        final double y = center.getY() + radiusY * radius * Math.sin(angle);
        final Location particleLocation = new Location(world, x, y, center.getZ());
        world.spawnParticle(Particle.DUST, particleLocation, 1, new DustOptions(Color.BLUE, 3));
      }
    }
  }

  private void spawnPortalFrame(
      final Location center,
      final int particleCount,
      final double radiusX,
      final double radiusY,
      final World world) {
    for (int i = 0; i < particleCount; i++) {
      final double angle = 2 * Math.PI * i / particleCount;
      final double x = center.getX() + radiusX * Math.cos(angle);
      final double y = center.getY() + radiusY * Math.sin(angle);
      final Location particleLocation = new Location(world, x, y, center.getZ());
      world.spawnParticle(Particle.DUST, particleLocation, 1, new DustOptions(Color.YELLOW, 3));
    }
  }
}
