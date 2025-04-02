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
package me.brandonli.murderrun.utils.screen;

import static java.util.Objects.requireNonNull;

import com.github.retrooper.packetevents.PacketEvents;
import com.github.retrooper.packetevents.PacketEventsAPI;
import com.github.retrooper.packetevents.manager.player.PlayerManager;
import com.github.retrooper.packetevents.wrapper.play.server.WrapperPlayServerCamera;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import me.brandonli.murderrun.MurderRun;
import org.bukkit.*;
import org.bukkit.attribute.Attribute;
import org.bukkit.attribute.AttributeInstance;
import org.bukkit.entity.Entity;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Player;
import org.bukkit.event.HandlerList;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.PlayerInventory;
import org.bukkit.plugin.PluginManager;
import org.bukkit.plugin.java.JavaPlugin;
import org.bukkit.scheduler.BukkitScheduler;
import org.bukkit.util.Vector;

public final class ScreenUtils {

  private ScreenUtils() {
    throw new UnsupportedOperationException("Utility class cannot be instantiated");
  }

  private static final Set<Player> TRACKED_PLAYERS = new HashSet<>();
  private static final ScreenListener SCREEN_LISTENER = new ScreenListener(TRACKED_PLAYERS);

  public static void init(final MurderRun plugin) {
    final Server server = Bukkit.getServer();
    final PluginManager pluginManager = server.getPluginManager();
    pluginManager.registerEvents(SCREEN_LISTENER, plugin);
  }

  public static void close() {
    HandlerList.unregisterAll(SCREEN_LISTENER);
  }

  public static void sendCameraEffect(final Player player, final CameraEffect type) {
    TRACKED_PLAYERS.add(player);
    final PlayerInventory inv = player.getInventory();
    final GameMode gm = player.getGameMode();
    final ItemStack[] invIs1 = inv.getContents();
    final ItemStack[] invIs2 = inv.getArmorContents();
    final double invHealth = player.getHealthScale();
    final int invFood = player.getFoodLevel();
    final GameMode invGM = player.getGameMode();
    final boolean invAllowFlight = player.getAllowFlight();
    final boolean invFlight = player.isFlying();
    final float invExp = player.getExp();
    final Vector invVelocity = player.getVelocity();
    final Map<Attribute, Double> attributes = new HashMap<>();
    final Registry<Attribute> registry = Registry.ATTRIBUTE;
    for (final Attribute attribute : registry) {
      final AttributeInstance instance = player.getAttribute(attribute);
      if (instance != null) {
        final double value = instance.getValue();
        attributes.put(attribute, value);
      }
    }

    final Location clone = applyMiddleManLogic(player, type, inv, gm);
    final MurderRun plugin = (MurderRun) JavaPlugin.getProvidingPlugin(MurderRun.class);
    final BukkitScheduler scheduler = Bukkit.getScheduler();
    scheduler.scheduleSyncDelayedTask(
      plugin,
      () -> {
        player.teleport(clone);
        inv.setContents(invIs1);
        inv.setArmorContents(invIs2);
        player.setHealthScale(invHealth);
        player.setFoodLevel(invFood);
        player.setGameMode(invGM);
        player.setAllowFlight(invAllowFlight);
        player.setFlying(invFlight);
        player.setExp(invExp);
        player.setVelocity(invVelocity);
        attributes.forEach((attribute, value1) -> {
          final double value = value1;
          final AttributeInstance instance = player.getAttribute(attribute);
          if (instance != null) {
            instance.setBaseValue(value);
          }
        });
        TRACKED_PLAYERS.remove(player);
      },
      5L
    );
  }

  private static Location applyMiddleManLogic(final Player player, final CameraEffect type, final PlayerInventory inv, final GameMode gm) {
    final Player.Spigot spigot = player.spigot();
    final Location location = player.getLocation();
    final Location clone = location.clone();
    final World world = requireNonNull(location.getWorld());
    final EntityType entityType = type.getMobType();
    final Entity e = world.spawnEntity(clone, entityType);
    player.setGameMode(GameMode.SPECTATOR);
    sendCameraPacket(player, e);
    e.remove();
    inv.clear();
    player.setExp(0);
    player.setGameMode(gm);
    player.setHealth(0);
    spigot.respawn();
    return clone;
  }

  private static void sendCameraPacket(final Player player, final Entity entity) {
    final int id = entity.getEntityId();
    final WrapperPlayServerCamera cameraPacket = new WrapperPlayServerCamera(id);
    final PacketEventsAPI<?> packetEvents = PacketEvents.getAPI();
    final PlayerManager manager = packetEvents.getPlayerManager();
    manager.sendPacket(player, cameraPacket);
  }
}
