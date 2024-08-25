package io.github.pulsebeat02.murderrun.game.player;

import static java.util.Objects.requireNonNull;
import static net.kyori.adventure.key.Key.key;

import io.github.pulsebeat02.murderrun.game.Game;
import io.github.pulsebeat02.murderrun.game.player.death.PlayerDeathTask;
import io.github.pulsebeat02.murderrun.game.scheduler.GameScheduler;
import io.github.pulsebeat02.murderrun.resourcepack.sound.SoundResource;
import java.util.Collection;
import java.util.UUID;
import java.util.function.Consumer;
import net.kyori.adventure.bossbar.BossBar;
import net.kyori.adventure.key.Key;
import net.kyori.adventure.sound.Sound.Source;
import net.kyori.adventure.text.Component;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.Particle;
import org.bukkit.World;
import org.bukkit.attribute.Attribute;
import org.bukkit.attribute.AttributeInstance;
import org.bukkit.block.Block;
import org.bukkit.entity.ArmorStand;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;
import org.bukkit.inventory.PlayerInventory;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;
import org.bukkit.util.RayTraceResult;
import org.bukkit.util.Vector;
import org.checkerframework.checker.nullness.qual.NonNull;
import org.checkerframework.checker.nullness.qual.Nullable;

public interface Participant {

  org.bukkit.entity.Player getInternalPlayer();

  default void removeAllGlowing() {
    this.apply(player -> {
      final Collection<? extends Player> viewers = Bukkit.getOnlinePlayers();
      viewers.forEach(this::removeEntityGlowingForPlayer);
    });
  }

  default void disableJump(final GameScheduler scheduler, final long ticks) {
    final AttributeInstance instance = this.getAttribute(Attribute.GENERIC_JUMP_STRENGTH);
    final double before = instance.getValue();
    instance.setBaseValue(0.0);
    scheduler.scheduleTask(() -> instance.setBaseValue(before), ticks);
  }

  default void disableWalkNoFOVEffects(final GameScheduler scheduler, final long ticks) {
    this.apply(player -> {
      final float before = player.getWalkSpeed();
      player.setWalkSpeed(0.0f);
      scheduler.scheduleTask(() -> player.setWalkSpeed(before), ticks);
    });
  }

  default void disableWalkWithFOVEffects(final int ticks) {
    this.addPotionEffects(new PotionEffect(PotionEffectType.SLOWNESS, ticks, Integer.MAX_VALUE));
  }

  default void apply(final Consumer<org.bukkit.entity.Player> consumer) {
    final org.bukkit.entity.Player player = this.getInternalPlayer();
    consumer.accept(player);
  }

  default void spawnParticle(
      final Particle particle,
      final Location location,
      final int count,
      final double offSetX,
      final double offSetY,
      final double offSetZ) {
    this.apply(
        player -> player.spawnParticle(particle, location, count, offSetX, offSetY, offSetZ));
  }

  default void addPotionEffects(final PotionEffect... effects) {
    this.apply(player -> {
      for (final PotionEffect effect : effects) {
        player.addPotionEffect(effect);
      }
    });
  }

  default @NonNull Location getLocation() {
    final org.bukkit.entity.Player player = this.getInternalPlayer();
    return player.getLocation();
  }

  default @Nullable Location getDeathLocation() {
    final org.bukkit.entity.Player player = this.getInternalPlayer();
    return player.getLastDeathLocation();
  }

  default PlayerInventory getInventory() {
    final org.bukkit.entity.Player player = this.getInternalPlayer();
    return player.getInventory();
  }

  default AttributeInstance getAttribute(final Attribute attribute) {
    final org.bukkit.entity.Player player = this.getInternalPlayer();
    return requireNonNull(player.getAttribute(attribute));
  }

  void sendMessage(final Component component);

  void showTitle(final Component title, final Component subtitle);

  void showBossBar(
      final Component name,
      final float progress,
      final BossBar.Color color,
      final BossBar.Overlay overlay);

  default void playSound(final SoundResource key) {
    this.playSound(key.getKey());
  }

  default void playSound(final Key key) {
    this.playSound(key, Source.MASTER, 1.0f, 1.0f);
  }

  default void playSound(final String key) {
    this.playSound(key(key));
  }

  void playSound(final Key key, final Source category, final float volume, final float pitch);

  void stopSound(final Key key);

  default void removeAllPotionEffects() {
    this.apply(player -> {
      final Collection<PotionEffect> effects = player.getActivePotionEffects();
      effects.forEach(effect -> {
        final PotionEffectType type = effect.getType();
        player.removePotionEffect(type);
      });
    });
  }

  void removeAllBossBars();

  default boolean canSeeEntity(final Entity entity, final double maxRangeSquared) {

    final Location entityLocation = entity.getLocation();
    final Location playerLocation = this.getLocation();
    final double distanceSquared = entityLocation.distanceSquared(playerLocation);
    if (distanceSquared > maxRangeSquared) {
      return false;
    }

    final World world = entity.getWorld();
    final Vector playerVector = playerLocation.toVector();
    final Vector entityVector = entityLocation.toVector();
    final Vector direction = playerVector.subtract(entityVector);
    final Vector normalizedDirection = direction.normalize();
    final RayTraceResult result =
        world.rayTraceBlocks(entityLocation, normalizedDirection, maxRangeSquared);
    if (result == null) {
      return true;
    }

    final Block block = result.getHitBlock();
    if (block == null) {
      return true;
    }

    final Material type = block.getType();
    return !type.isSolid();
  }

  void setEntityGlowingForPlayer(final Entity entity, final ChatColor color);

  void removeEntityGlowingForPlayer(final Entity entity);

  void addFakeWorldBorderEffect();

  default void removeFakeWorldBorderEffect() {
    this.apply(player -> player.setWorldBorder(null));
  }

  void hideNameTag(final GameScheduler scheduler, final long ticks);

  void showNameTag();

  default void teleport(final Location location) {
    this.apply(player -> player.teleport(location));
  }

  void addDeathTask(final PlayerDeathTask task);

  void removeDeathTask(final PlayerDeathTask task);

  Collection<PlayerDeathTask> getDeathTasks();

  UUID getUuid();

  boolean isAlive();

  void setAlive(final boolean alive);

  Game getGame();

  default void setEntityGlowingForPlayer(final GamePlayer gamePlayer, final ChatColor color) {
    gamePlayer.apply(player -> this.setEntityGlowingForPlayer(player, color));
  }

  default void removeEntityGlowingForPlayer(final GamePlayer player) {
    this.apply(player::removeEntityGlowingForPlayer);
  }

  @Nullable
  ArmorStand getCorpse();

  void setCorpse(@Nullable ArmorStand corpse);
}
