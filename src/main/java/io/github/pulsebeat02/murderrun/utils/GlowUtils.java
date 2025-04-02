/*

MIT License

Copyright (c) 2025 Brandon Li

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
package io.github.pulsebeat02.murderrun.utils;

import static java.util.Objects.requireNonNull;

import com.github.retrooper.packetevents.PacketEvents;
import com.github.retrooper.packetevents.PacketEventsAPI;
import com.github.retrooper.packetevents.manager.player.PlayerManager;
import com.github.retrooper.packetevents.protocol.entity.data.EntityData;
import com.github.retrooper.packetevents.protocol.entity.data.EntityDataTypes;
import com.github.retrooper.packetevents.wrapper.play.server.WrapperPlayServerEntityMetadata;
import com.google.common.collect.HashBasedTable;
import com.google.common.collect.Table;
import io.github.pulsebeat02.murderrun.MurderRun;
import java.util.ArrayList;
import java.util.List;
import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.block.Block;
import org.bukkit.entity.*;
import org.bukkit.plugin.java.JavaPlugin;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;
import org.checkerframework.checker.nullness.qual.Nullable;

public final class GlowUtils {

  private static final Table<Player, Location, Slime> GLOWING_BLOCKS = HashBasedTable.create();
  private static final PotionEffect GLOWING_EFFECT = new PotionEffect(PotionEffectType.GLOWING, PotionEffect.INFINITE_DURATION, 0);

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
      });
      watcher.showEntity(plugin, spawned);
      GLOWING_BLOCKS.put(watcher, target, spawned);
    } else {
      final Slime slime = GLOWING_BLOCKS.remove(watcher, target);
      if (slime != null) {
        slime.remove();
      }
    }
    return GLOWING_BLOCKS.get(watcher, target);
  }
}
