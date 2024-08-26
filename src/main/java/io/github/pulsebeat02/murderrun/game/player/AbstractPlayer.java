package io.github.pulsebeat02.murderrun.game.player;

import static java.util.Objects.requireNonNull;
import static net.kyori.adventure.key.Key.key;

import io.github.pulsebeat02.murderrun.game.scheduler.GameScheduler;
import io.github.pulsebeat02.murderrun.resourcepack.sound.SoundResource;
import java.util.Collection;
import java.util.function.Consumer;
import net.kyori.adventure.key.Key;
import net.kyori.adventure.sound.Sound.Source;
import org.bukkit.Location;
import org.bukkit.Particle;
import org.bukkit.attribute.Attribute;
import org.bukkit.attribute.AttributeInstance;
import org.bukkit.entity.Player;
import org.bukkit.inventory.PlayerInventory;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;
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
  public void spawnParticle(
      final Particle particle,
      final Location location,
      final int count,
      final double offSetX,
      final double offSetY,
      final double offSetZ) {
    this.apply(
        player -> player.spawnParticle(particle, location, count, offSetX, offSetY, offSetZ));
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
  public void playSound(final SoundResource key) {
    this.playSound(key.getKey());
  }

  @Override
  public void playSound(final Key key) {
    this.playSound(key, Source.MASTER, 1.0f, 1.0f);
  }

  @Override
  public void playSound(final String key) {
    this.playSound(key(key));
  }

  @Override
  public void removeAllPotionEffects() {
    this.apply(player -> {
      final Collection<PotionEffect> effects = player.getActivePotionEffects();
      effects.forEach(effect -> {
        final PotionEffectType type = effect.getType();
        player.removePotionEffect(type);
      });
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
}
