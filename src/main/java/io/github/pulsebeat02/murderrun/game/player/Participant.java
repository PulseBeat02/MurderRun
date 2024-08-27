package io.github.pulsebeat02.murderrun.game.player;

import io.github.pulsebeat02.murderrun.game.Game;
import io.github.pulsebeat02.murderrun.game.player.death.DeathManager;
import io.github.pulsebeat02.murderrun.game.scheduler.GameScheduler;
import java.util.UUID;
import java.util.function.Consumer;
import org.bukkit.Location;
import org.bukkit.Particle;
import org.bukkit.attribute.Attribute;
import org.bukkit.attribute.AttributeInstance;
import org.bukkit.entity.ArmorStand;
import org.bukkit.entity.Player;
import org.bukkit.inventory.PlayerInventory;
import org.bukkit.potion.PotionEffect;
import org.checkerframework.checker.nullness.qual.NonNull;
import org.checkerframework.checker.nullness.qual.Nullable;

public interface Participant {

  Player getInternalPlayer();

  void disableJump(final GameScheduler scheduler, final long ticks);

  void disableWalkNoFOVEffects(final GameScheduler scheduler, final long ticks);

  void disableWalkWithFOVEffects(final int ticks);

  void apply(final Consumer<Player> consumer);

  void spawnParticle(
      final Particle particle,
      final Location location,
      final int count,
      final double offSetX,
      final double offSetY,
      final double offSetZ);

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

  @Nullable
  ArmorStand getCorpse();

  void setCorpse(@Nullable ArmorStand corpse);

  MetadataManager getMetadataManager();

  DeathManager getDeathManager();

  PlayerAudience getAudience();

  String getDisplayName();
}
