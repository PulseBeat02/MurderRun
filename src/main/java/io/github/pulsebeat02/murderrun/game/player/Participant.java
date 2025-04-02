/*

MIT License

Copyright (c) 2024 Brandon Li

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
package io.github.pulsebeat02.murderrun.game.player;

import io.github.pulsebeat02.murderrun.game.Game;
import io.github.pulsebeat02.murderrun.game.ability.Ability;
import io.github.pulsebeat02.murderrun.game.player.death.DeathManager;
import io.github.pulsebeat02.murderrun.game.scheduler.GameScheduler;
import java.util.Collection;
import java.util.Map;
import java.util.UUID;
import java.util.function.Consumer;
import java.util.function.Function;
import org.bukkit.GameMode;
import org.bukkit.Location;
import org.bukkit.Particle;
import org.bukkit.WorldBorder;
import org.bukkit.attribute.Attribute;
import org.bukkit.attribute.AttributeInstance;
import org.bukkit.entity.Entity;
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

  void resetAllAttributes();

  Scoreboard getScoreboard();

  void setWorldBorder(final WorldBorder border);

  void kick(final String message);

  void sendEquipmentChange(final EquipmentSlot slot, final ItemStack item);

  Collection<Class<? extends Ability>> getAbilities();

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
}
