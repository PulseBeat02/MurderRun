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
package me.brandonli.murderrun.game.player;

import com.github.retrooper.packetevents.wrapper.PacketWrapper;
import java.util.Map;
import java.util.UUID;
import java.util.function.Consumer;
import java.util.function.Function;
import me.brandonli.murderrun.game.Game;
import me.brandonli.murderrun.game.player.death.DeathManager;
import me.brandonli.murderrun.game.player.metadata.MetadataManager;
import me.brandonli.murderrun.game.scheduler.GameScheduler;
import org.bukkit.GameMode;
import org.bukkit.Location;
import org.bukkit.Particle;
import org.bukkit.WorldBorder;
import org.bukkit.attribute.Attribute;
import org.bukkit.attribute.AttributeInstance;
import org.bukkit.block.Block;
import org.bukkit.entity.Entity;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;
import org.bukkit.inventory.EquipmentSlot;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.PlayerInventory;
import org.bukkit.persistence.PersistentDataContainer;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;
import org.bukkit.scoreboard.Scoreboard;
import org.bukkit.util.Vector;
import org.checkerframework.checker.nullness.qual.NonNull;
import org.checkerframework.checker.nullness.qual.Nullable;

public interface Participant {
  Player getInternalPlayer();

  void disableJump(final GameScheduler scheduler, final long ticks);

  void disableWalkNoFOVEffects(final GameScheduler scheduler, final long ticks);

  void disableWalkWithFOVEffects(final int ticks);

  void apply(final Consumer<Player> consumer);

  <T> T applyFunction(final Function<Player, T> function);

  void spawnPlayerSpecificParticle(final Particle particle);

  void addPotionEffects(final PotionEffect... effects);

  @NonNull
  Location getLocation();

  @Nullable
  Location getDeathLocation();

  PlayerInventory getInventory();

  @Nullable
  AttributeInstance getAttribute(final Attribute attribute);

  void removeAllPotionEffects();

  void teleport(final Location location);

  UUID getUUID();

  boolean isAlive();

  void setAlive(final boolean alive);

  Game getGame();

  MetadataManager getMetadataManager();

  DeathManager getDeathManager();

  PlayerAudience getAudience();

  String getDisplayName();

  void setGravity(final boolean gravity);

  void setFreezeTicks(final int ticks);

  void removePotionEffect(final PotionEffectType type);

  void setVelocity(final Vector vector);

  void setCanDismount(final boolean canDismount);

  boolean canDismount();

  void setFallDistance(final float distance);

  void setInvulnerable(final boolean invulnerable);

  void setGameMode(final GameMode mode);

  void clearInventory();

  void setRespawnLocation(final Location location, final boolean force);

  void setHealth(final double health);

  double getHealth();

  void setFoodLevel(final int foodLevel);

  void setSaturation(final float saturation);

  void setAllowSpectatorTeleport(final boolean allow);

  boolean canSpectatorTeleport();

  void setSpectatorTarget(final Entity entity);

  void setLastDeathLocation(final Location location);

  void setAllowFlight(final boolean allow);

  void setFlySpeed(final float speed);

  void setLevel(final int level);

  void setLastPortalUse(final long cooldown);

  long getLastPortalUse();

  float getFlySpeed();

  Vector getVelocity();

  void setWalkSpeed(final float speed);

  void setExp(final float exp);

  void setGlowing(final boolean glowing);

  void setFireTicks(final int ticks);

  void stopAllSounds();

  void setLoggingOut(final boolean loggingOut);

  boolean isLoggingOut();

  String getName();

  PersistentDataContainer getPersistentDataContainer();

  Location getEyeLocation();

  Map<Attribute, Double> getDefaultAttributes();

  @Nullable
  Double getDefaultAttribute(final Attribute attribute);

  void resetAllAttributes();

  Scoreboard getScoreboard();

  void setWorldBorder(final WorldBorder border);

  void kick(final String message);

  void sendEquipmentChange(final EquipmentSlot slot, final ItemStack item);

  <T> void spawnParticle(
    final Particle particle,
    final Location location,
    final int count,
    final double offsetX,
    final double offsetY,
    final double offsetZ,
    final double extra,
    final @Nullable T data
  );

  int getCooldown(final ItemStack item);

  void setCooldown(final ItemStack item, final int cooldown);

  void setAbilityCooldowns(final String cooldown, final int seconds);

  boolean hasAbility(final String ability);

  boolean isSprinting();

  int getFoodLevel();

  boolean getInternalDeathState();

  @Nullable
  Block getTargetBlockExact(final int max);

  void sendPacket(final PacketWrapper<?> wrapper);

  boolean isValid();

  void resetAttribute(final Attribute attribute);

  void sendPotionEffectChange(final LivingEntity entity, final PotionEffect effect);

  void sendPotionEffectChangeRemove(final LivingEntity entity, final PotionEffectType type);

  void sendPotionEffectChange(final PotionEffect effect);

  void sendPotionEffectChangeRemove(final PotionEffectType type);

  boolean isSneaking();
}
