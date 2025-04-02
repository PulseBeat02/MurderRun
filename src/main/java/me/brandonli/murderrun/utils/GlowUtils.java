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
package me.brandonli.murderrun.utils;

import static java.util.Objects.requireNonNull;

import com.github.retrooper.packetevents.PacketEvents;
import com.github.retrooper.packetevents.PacketEventsAPI;
import com.github.retrooper.packetevents.manager.player.PlayerManager;
import com.github.retrooper.packetevents.protocol.entity.data.EntityData;
import com.github.retrooper.packetevents.protocol.entity.data.EntityDataTypes;
import com.github.retrooper.packetevents.wrapper.play.server.WrapperPlayServerEntityMetadata;
import com.google.common.collect.HashBasedTable;
import com.google.common.collect.Table;
import java.util.ArrayList;
import java.util.List;
import me.brandonli.murderrun.MurderRun;
import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.block.Block;
import org.bukkit.entity.*;
import org.bukkit.plugin.java.JavaPlugin;
import org.checkerframework.checker.nullness.qual.Nullable;

public final class GlowUtils {

  private static final Table<Player, Location, Slime> GLOWING_BLOCKS = HashBasedTable.create();

  public GlowUtils() {
    throw new UnsupportedOperationException("Utility class cannot be instantiated");
  }

  public static void setEntityGlowing(final Player watcher, final Entity entity, final boolean glowing) {
    final byte flags = (byte) ((entity.getFireTicks() > 0 ? 0x01 : 0) |
      (entity instanceof final Player player && player.isSneaking() ? 0x02 : 0) |
      (entity instanceof final Player player && player.isSprinting() ? 0x08 : 0) |
      (entity instanceof final LivingEntity livingEntity && livingEntity.isSwimming() ? 0x10 : 0) |
      (entity instanceof final LivingEntity livingEntity && livingEntity.isInvisible() ? 0x20 : 0) |
      (glowing ? 0x40 : 0) |
      (entity instanceof final LivingEntity livingEntity && livingEntity.isGliding() ? 0x80 : 0));
    final EntityData entityData = new EntityData(0, EntityDataTypes.BYTE, flags);
    final List<EntityData> metadata = new ArrayList<>();
    metadata.add(entityData);

    final int id = entity.getEntityId();
    final WrapperPlayServerEntityMetadata packet = new WrapperPlayServerEntityMetadata(id, metadata);

    final PacketEventsAPI<?> packetEvents = PacketEvents.getAPI();
    final PlayerManager manager = packetEvents.getPlayerManager();
    manager.sendPacket(watcher, packet);
  }

  public static @Nullable Slime setBlockGlowing(final Player watcher, final Location target, final boolean glowing) {
    final Block block = target.getBlock();
    final World world = block.getWorld();
    final Location blockLocation = block.getLocation();
    final double centerX = blockLocation.getBlockX() + 0.5;
    final double centerY = blockLocation.getBlockY();
    final double centerZ = blockLocation.getBlockZ() + 0.5;
    final Location spawnLocation = new Location(world, centerX, centerY, centerZ);
    if (glowing) {
      final MurderRun plugin = (MurderRun) JavaPlugin.getProvidingPlugin(MurderRun.class);
      if (GLOWING_BLOCKS.contains(watcher, target)) {
        return requireNonNull(GLOWING_BLOCKS.get(watcher, target));
      }
      final Slime spawned = world.spawn(spawnLocation, Slime.class, slime -> {
        slime.setSize(2);
        slime.setGlowing(true);
        slime.setInvisible(true);
        slime.setAI(false);
        slime.setCollidable(false);
        slime.setInvulnerable(true);
        slime.setVisibleByDefault(false);
        watcher.showEntity(plugin, slime);
      });
      GLOWING_BLOCKS.put(watcher, target, spawned);
    } else {
      final Slime slime = GLOWING_BLOCKS.remove(watcher, target);
      if (slime != null) {
        slime.remove();
        return slime;
      }
    }
    return GLOWING_BLOCKS.get(watcher, target);
  }
}
