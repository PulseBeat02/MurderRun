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

import static java.util.Objects.requireNonNull;

import io.github.pulsebeat02.murderrun.game.scheduler.GameScheduler;
import io.github.pulsebeat02.murderrun.game.scheduler.reference.StrictPlayerReference;
import io.github.pulsebeat02.murderrun.immutable.Keys;
import io.github.pulsebeat02.murderrun.utils.PDCUtils;
import java.util.Collection;
import java.util.Map;
import java.util.Set;
import java.util.UUID;
import java.util.function.Consumer;
import java.util.function.Function;
import org.bukkit.*;
import org.bukkit.attribute.Attribute;
import org.bukkit.attribute.AttributeInstance;
import org.bukkit.block.Block;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;
import org.bukkit.inventory.EquipmentSlot;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.PlayerInventory;
import org.bukkit.persistence.PersistentDataContainer;
import org.bukkit.persistence.PersistentDataType;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;
import org.bukkit.scoreboard.Scoreboard;
import org.bukkit.util.Vector;
import org.checkerframework.checker.nullness.qual.KeyFor;
import org.checkerframework.checker.nullness.qual.NonNull;
import org.checkerframework.checker.nullness.qual.Nullable;

public abstract class AbstractPlayer implements Participant {

  @Override
  public void disableJump(final GameScheduler scheduler, final long ticks) {
    final StrictPlayerReference reference = StrictPlayerReference.of(this);
    final AttributeInstance instance = requireNonNull(this.getAttribute(Attribute.JUMP_STRENGTH));
    final double before = instance.getValue();
    instance.setBaseValue(0.0);
    scheduler.scheduleTask(() -> instance.setBaseValue(before), ticks, reference);
  }

  @Override
  public void disableWalkNoFOVEffects(final GameScheduler scheduler, final long ticks) {
    final StrictPlayerReference reference = StrictPlayerReference.of(this);
    this.apply(player -> {
        final float before = player.getWalkSpeed();
        player.setWalkSpeed(0.0f);
        scheduler.scheduleTask(() -> player.setWalkSpeed(before), ticks, reference);
      });
  }

  @Override
  public void disableWalkWithFOVEffects(final int ticks) {
    this.addPotionEffects(new PotionEffect(PotionEffectType.SLOWNESS, ticks, Integer.MAX_VALUE));
  }

  @Override
  public void apply(final Consumer<Player> consumer) {
    this.applyFunction(player -> {
        consumer.accept(player);
        return null;
      });
  }

  @Override
  public <T> T applyFunction(final Function<Player, T> function) {
    final Player player = this.getInternalPlayer();
    return function.apply(player);
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
    return this.applyFunction(Player::getLocation);
  }

  @Override
  public @Nullable Location getDeathLocation() {
    return this.applyFunction(Player::getLastDeathLocation);
  }

  @Override
  public PlayerInventory getInventory() {
    return this.applyFunction(Player::getInventory);
  }

  @Override
  public @Nullable AttributeInstance getAttribute(final Attribute attribute) {
    return this.applyFunction(player -> player.getAttribute(attribute));
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
    return this.applyFunction(Player::getDisplayName);
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
    return this.applyFunction(Player::getUniqueId);
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
    final PlayerInventory inventory = this.getInventory();
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
    return this.applyFunction(Player::getHealth);
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
    return this.applyFunction(Player::getFlySpeed);
  }

  @Override
  public Vector getVelocity() {
    return this.applyFunction(Player::getVelocity);
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
    return this.applyFunction(Player::getName);
  }

  @Override
  public PersistentDataContainer getPersistentDataContainer() {
    return this.applyFunction(Player::getPersistentDataContainer);
  }

  @Override
  public Location getEyeLocation() {
    return this.applyFunction(Player::getEyeLocation);
  }

  @Override
  public void resetAllAttributes() {
    final Map<Attribute, Double> attributes = this.getDefaultAttributes();
    final Set<Map.Entry<@KeyFor("attributes") Attribute, Double>> entries = attributes.entrySet();
    for (final Map.Entry<Attribute, Double> entry : entries) {
      final Attribute attribute = entry.getKey();
      final double value = entry.getValue();
      final AttributeInstance instance = requireNonNull(this.getAttribute(attribute));
      instance.setBaseValue(value);
    }
  }

  @Override
  public Scoreboard getScoreboard() {
    return this.applyFunction(Player::getScoreboard);
  }

  @Override
  public void setWorldBorder(final @Nullable WorldBorder border) {
    this.apply(player -> player.setWorldBorder(border));
  }

  @Override
  public void kick(final String message) {
    this.apply(player -> player.kickPlayer(message));
  }

  @Override
  public void sendEquipmentChange(final EquipmentSlot slot, final ItemStack item) {
    this.apply(player -> player.sendEquipmentChange(player, slot, item));
  }

  @Override
  public <T> void spawnParticle(
    final Particle particle,
    final Location location,
    final int count,
    final double offsetX,
    final double offsetY,
    final double offsetZ,
    final double extra,
    final @Nullable T data
  ) {
    this.apply(player -> player.spawnParticle(particle, location, count, offsetX, offsetY, offsetZ, extra, data));
  }

  @Override
  public int getCooldown(final ItemStack item) {
    return this.applyFunction(player -> player.getCooldown(item));
  }

  @Override
  public void setCooldown(final ItemStack item, final int cooldown) {
    this.apply(player -> player.setCooldown(item, cooldown));
  }

  @Override
  public void setAbilityCooldowns(final String ability, final int seconds) {
    final PlayerInventory inventory = this.getInventory();
    final ItemStack[] items = inventory.getContents();
    for (final ItemStack item : items) {
      if (!PDCUtils.isAbility(item)) {
        continue;
      }
      final String abilityName = PDCUtils.getPersistentDataAttribute(item, Keys.ABILITY_KEY_NAME, PersistentDataType.STRING);
      if (!ability.equals(abilityName)) {
        return;
      }
      this.setCooldown(item, seconds);
    }
  }

  @Override
  public boolean hasAbility(final String ability) {
    final PlayerInventory inventory = this.getInventory();
    final ItemStack[] items = inventory.getContents();
    for (final ItemStack item : items) {
      if (!PDCUtils.isAbility(item)) {
        continue;
      }
      final String abilityName = PDCUtils.getPersistentDataAttribute(item, Keys.ABILITY_KEY_NAME, PersistentDataType.STRING);
      if (ability.equals(abilityName)) {
        return true;
      }
    }
    return false;
  }

  @Override
  public boolean isSprinting() {
    return this.applyFunction(Player::isSprinting);
  }

  @Override
  public int getFoodLevel() {
    return this.applyFunction(Player::getFoodLevel);
  }

  @Override
  public boolean getInternalDeathState() {
    return this.applyFunction(Player::isDead);
  }

  @Override
  public @Nullable Block getTargetBlockExact(final int max) {
    return this.applyFunction(player -> player.getTargetBlockExact(max));
  }
}
