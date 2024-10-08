package io.github.pulsebeat02.murderrun.game.player;

import static java.util.Objects.requireNonNull;

import io.github.pulsebeat02.murderrun.game.scheduler.GameScheduler;
import java.util.Collection;
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
import org.bukkit.persistence.PersistentDataContainer;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;
import org.bukkit.util.Vector;
import org.checkerframework.checker.nullness.qual.NonNull;
import org.checkerframework.checker.nullness.qual.Nullable;

public abstract class AbstractPlayer implements Participant {

  @Override
  public void disableJump(final GameScheduler scheduler, final long ticks) {
    final AttributeInstance instance = this.getAttribute(Attribute.GENERIC_JUMP_STRENGTH);
    final double before = instance.getValue();
    instance.setBaseValue(0.0);
    scheduler.scheduleTask(() -> instance.setBaseValue(before), ticks);
  }

  @Override
  public void disableWalkNoFOVEffects(final GameScheduler scheduler, final long ticks) {
    this.apply(player -> {
        final float before = player.getWalkSpeed();
        player.setWalkSpeed(0.0f);
        scheduler.scheduleTask(() -> player.setWalkSpeed(before), ticks);
      });
  }

  @Override
  public void disableWalkWithFOVEffects(final int ticks) {
    this.addPotionEffects(new PotionEffect(PotionEffectType.SLOWNESS, ticks, Integer.MAX_VALUE));
  }

  @Override
  public void apply(final Consumer<Player> consumer) {
    final Player player = this.getInternalPlayer();
    consumer.accept(player);
  }

  @Override
  public void spawnPlayerSpecificParticle(final Particle particle) {
    this.apply(player -> player.spawnParticle(particle, 1, 0, 0, 0));
  }

  @Override
  public void addPotionEffects(final PotionEffect... effects) {
    this.apply(player -> {
        for (final PotionEffect effect : effects) {
          player.addPotionEffect(effect);
        }
      });
  }

  @Override
  public @NonNull Location getLocation() {
    final Player player = this.getInternalPlayer();
    return player.getLocation();
  }

  @Override
  public @Nullable Location getDeathLocation() {
    final Player player = this.getInternalPlayer();
    return player.getLastDeathLocation();
  }

  @Override
  public PlayerInventory getInventory() {
    final Player player = this.getInternalPlayer();
    return player.getInventory();
  }

  @Override
  public AttributeInstance getAttribute(final Attribute attribute) {
    final Player player = this.getInternalPlayer();
    return requireNonNull(player.getAttribute(attribute));
  }

  @Override
  public void removeAllPotionEffects() {
    this.apply(player -> {
        final Collection<PotionEffect> effects = player.getActivePotionEffects();
        for (final PotionEffect effect : effects) {
          final PotionEffectType type = effect.getType();
          player.removePotionEffect(type);
        }
      });
  }

  @Override
  public void teleport(final Location location) {
    this.apply(player -> player.teleport(location));
  }

  @Override
  public String getDisplayName() {
    final Player player = this.getInternalPlayer();
    return player.getDisplayName();
  }

  @Override
  public void setGravity(final boolean gravity) {
    this.apply(player -> player.setGravity(gravity));
  }

  @Override
  public void setFreezeTicks(final int ticks) {
    this.apply(player -> player.setFreezeTicks(ticks));
  }

  @Override
  public void removePotionEffect(final PotionEffectType type) {
    this.apply(player -> player.removePotionEffect(type));
  }

  @Override
  public void setVelocity(final Vector vector) {
    this.apply(player -> player.setVelocity(vector));
  }

  @Override
  public UUID getUUID() {
    final Player player = this.getInternalPlayer();
    return player.getUniqueId();
  }

  @Override
  public void setFallDistance(final float distance) {
    this.apply(player -> player.setFallDistance(distance));
  }

  @Override
  public void setInvulnerable(final boolean invulnerable) {
    this.apply(player -> player.setInvulnerable(invulnerable));
  }

  @Override
  public void setGameMode(final GameMode mode) {
    this.apply(player -> player.setGameMode(mode));
  }

  @Override
  public void setRespawnLocation(final Location location, final boolean force) {
    this.apply(player -> player.setRespawnLocation(location, force));
  }

  @Override
  public void clearInventory() {
    final Player player = this.getInternalPlayer();
    final PlayerInventory inventory = player.getInventory();
    inventory.clear();
  }

  @Override
  public void setSaturation(final float saturation) {
    this.apply(player -> player.setSaturation(saturation));
  }

  @Override
  public void setFoodLevel(final int foodLevel) {
    this.apply(player -> player.setFoodLevel(foodLevel));
  }

  @Override
  public void setHealth(final double health) {
    this.apply(player -> player.setHealth(health));
  }

  @Override
  public void setSpectatorTarget(final @Nullable Entity entity) {
    this.apply(player -> player.setSpectatorTarget(entity));
  }

  @Override
  public void setLastDeathLocation(final @Nullable Location location) {
    this.apply(player -> player.setLastDeathLocation(location));
  }

  @Override
  public double getHealth() {
    final Player player = this.getInternalPlayer();
    return player.getHealth();
  }

  @Override
  public void setFlySpeed(final float speed) {
    this.apply(player -> player.setFlySpeed(speed));
  }

  @Override
  public void setAllowFlight(final boolean allow) {
    this.apply(player -> player.setAllowFlight(allow));
  }

  @Override
  public void setLevel(final int level) {
    this.apply(player -> player.setLevel(level));
  }

  @Override
  public float getFlySpeed() {
    final Player player = this.getInternalPlayer();
    return player.getFlySpeed();
  }

  @Override
  public Vector getVelocity() {
    final Player player = this.getInternalPlayer();
    return player.getVelocity();
  }

  @Override
  public void stopAllSounds() {
    this.apply(Player::stopAllSounds);
  }

  @Override
  public void setFireTicks(final int ticks) {
    this.apply(player -> player.setFireTicks(ticks));
  }

  @Override
  public void setGlowing(final boolean glowing) {
    this.apply(player -> player.setGlowing(glowing));
  }

  @Override
  public void setExp(final float exp) {
    this.apply(player -> player.setExp(exp));
  }

  @Override
  public void setWalkSpeed(final float speed) {
    this.apply(player -> player.setWalkSpeed(speed));
  }

  @Override
  public String getName() {
    final Player player = this.getInternalPlayer();
    return player.getName();
  }

  @Override
  public PersistentDataContainer getPersistentDataContainer() {
    final Player player = this.getInternalPlayer();
    return player.getPersistentDataContainer();
  }
}
