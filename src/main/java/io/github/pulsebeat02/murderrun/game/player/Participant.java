package io.github.pulsebeat02.murderrun.game.player;

import io.github.pulsebeat02.murderrun.game.Game;
import io.github.pulsebeat02.murderrun.game.player.death.DeathManager;
import io.github.pulsebeat02.murderrun.game.scheduler.GameScheduler;
import io.github.pulsebeat02.murderrun.resourcepack.sound.SoundResource;
import java.util.UUID;
import java.util.function.Consumer;
import net.kyori.adventure.bossbar.BossBar;
import net.kyori.adventure.key.Key;
import net.kyori.adventure.sound.Sound.Source;
import net.kyori.adventure.text.Component;
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

  void sendMessage(final Component component);

  void showTitle(final Component title, final Component subtitle);

  void showBossBar(
      final Component name,
      final float progress,
      final BossBar.Color color,
      final BossBar.Overlay overlay);

  void playSound(final SoundResource key);

  void playSound(final Key key);

  void playSound(final String key);

  void playSound(final Key key, final Source category, final float volume, final float pitch);

  void stopSound(final Key key);

  void removeAllPotionEffects();

  void removeAllBossBars();

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

  String getDisplayName();
}
