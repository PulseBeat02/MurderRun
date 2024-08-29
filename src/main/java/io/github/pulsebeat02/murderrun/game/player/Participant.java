package io.github.pulsebeat02.murderrun.game.player;

import io.github.pulsebeat02.murderrun.game.Game;
import io.github.pulsebeat02.murderrun.game.player.death.DeathManager;
import io.github.pulsebeat02.murderrun.game.scheduler.GameScheduler;
import java.util.UUID;
import java.util.function.Consumer;
import org.bukkit.GameMode;
import org.bukkit.Location;
import org.bukkit.Particle;
import org.bukkit.attribute.Attribute;
import org.bukkit.attribute.AttributeInstance;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;
import org.bukkit.inventory.PlayerInventory;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;
import org.bukkit.util.Vector;
import org.checkerframework.checker.nullness.qual.NonNull;
import org.checkerframework.checker.nullness.qual.Nullable;

public interface Participant {

  Player getInternalPlayer();

  void disableJump(final GameScheduler scheduler, final long ticks);

  void disableWalkNoFOVEffects(final GameScheduler scheduler, final long ticks);

  void disableWalkWithFOVEffects(final int ticks);

  void apply(final Consumer<Player> consumer);

  void spawnPlayerSpecificParticle(final Particle particle);

  void addPotionEffects(final PotionEffect... effects);

  @NonNull
  Location getLocation();

  @Nullable
  Location getDeathLocation();

  PlayerInventory getInventory();

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
}
